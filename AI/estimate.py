import re
from langchain_core.prompts import PromptTemplate
from langchain_openai import ChatOpenAI
from langchain.chains.llm import LLMChain
from langchain.chains.combine_documents.stuff import StuffDocumentsChain

def run_hybrid_rag_query(vectordb, query):
    retriever = vectordb.as_retriever(search_kwargs={"k": 3})
    docs = retriever.invoke(query)

    prompt_template = PromptTemplate(
        input_variables=["context", "question"],
        template="""
당신은 공공시설물의 철거·수리 등의 견적을 계산하는 전문 엔지니어입니다.
아래는 검색된 참고 문서입니다. 이 문서들에는 가격 산정의 근거가 포함되어 있습니다.

당신의 임무는 다음과 같습니다:
1. 문서에서 사용 가능한 정보를 최대한 활용해 예상 견적을 '원 단위'로 계산합니다.
2. 문서에 직접적 근거가 부족할 경우, 자신의 지식으로 추정하거나 일반적인 단가와 자재비, 인건비 등을 가정하여 최대한 설득력 있는 계산을 수행합니다.
3. '정확하지 않다', '모르겠다'는 표현은 피하고, 대략적인 수치라도 반드시 추정하여 출력하세요.
4. 계산은 다음과 같은 형식으로 정리하세요:

예상 견적 (원): [숫자]
📌 계산 근거 요약: [단가, 인건비, 장비비, 수량 등의 가정 설명]
📚 참고 문서 내용 요약: [검색 또는 참조된 문서 요약]

해당 시설물의 철거 또는 수리 견적이 문서에서 직접 제공되지 않더라도, 반드시 다음 기준에 따라 근사 추정을 시도해 주세요:

- 일반적인 공공시설물 단가
- 자재 및 장비 사용 시뮬레이션
- 인건비 기준
- 한국의 지방자치단체 발주 공사 사례

직접 정보가 없어도, LLM이 보유한 통계나 단가 기준, 논리적 추정에 기반하여 **원 단위 견적**을 반드시 출력해 주세요.

문서:
{context}

질문:
{question}

답변:
""".strip()
    )

    llm = ChatOpenAI(model_name="gpt-5")
    llm_chain = LLMChain(llm=llm, prompt=prompt_template)
    stuff_chain = StuffDocumentsChain(
        llm_chain=llm_chain,
        document_variable_name="context"
    )

    raw_answer = stuff_chain.invoke({
        "input_documents": docs,
        "question": query
    })

    if isinstance(raw_answer, dict) and "output_text" in raw_answer:
        output_text = raw_answer["output_text"]
    elif isinstance(raw_answer, str):
        output_text = raw_answer
    else:
        output_text = str(raw_answer)

    # 견적 추출
    estimate_match = re.search(r"예상\s*견적\s*\(원\)\s*[:：]\s*([\d,]+)", output_text)
    try:
        estimate_val = int(estimate_match.group(1).replace(",", "")) if estimate_match else None
    except:
        estimate_val = None

    # 계산 근거 요약
    basis_match = re.search(r"📌\s*계산\s*근거\s*요약\s*[:：]\s*(.+?)(?=\n📚|\Z)", output_text, re.S)
    basis_text = basis_match.group(1).strip() if basis_match else ""

    # 참고 문서 내용 요약
    ref_match = re.search(r"📚\s*참고\s*문서\s*내용\s*요약\s*[:：]\s*(.+)", output_text, re.S)
    ref_text = ref_match.group(1).strip() if ref_match else ""

    # 📌 라벨 포함해서 합치기
    if basis_text or ref_text:
        estimate_basis = ""
        if basis_text:
            estimate_basis += "📌 계산 근거 요약:\n" + basis_text
        if ref_text:
            if estimate_basis:
                estimate_basis += "\n\n"
            estimate_basis += "📚 참고 문서 내용 요약:\n" + ref_text
    else:
        estimate_basis = ""

    meta_docs = [
        {
            "id": getattr(doc, "id", None),
            "metadata": doc.metadata
        }
        for doc in docs
    ]

    return {
        "estimate": estimate_val,
        "estimate_basis": estimate_basis,
        "raw_answer": output_text
    }, meta_docs
