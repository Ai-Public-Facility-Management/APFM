import json
from langchain_core.prompts import ChatPromptTemplate
from langchain_openai import ChatOpenAI
from .schema import parser

def generate_proposal(
    estimations: list
    # 필요하다면 다른 항목들(project_overview 등)도 추가 가능
) -> dict:
    estimations_json = json.dumps(estimations, ensure_ascii=False, indent=2)

    document_format = """
    [공 사 명] {project_name}
    [공사개요] {project_overview}
    [공사 소요 기간] {construction_period}
    [현장 분석 요약] {site_analysis_summary}
    [세부 견적 및 계산 근거 요약] {estimation_details_with_basis}
    [총 금액] {total_cost}
    [작업 인력 계획] {manpower_plan}
    [장비 계획] {equipment_plan}
    [안전 및 품질관리 방안] {safety_quality_plan}
    [결론 및 기대효과] {conclusion_expected_effect}
    """

    prompt = ChatPromptTemplate.from_template("""
    당신은 건설 입찰 제안서 작성 전문가입니다.
    아래 '문서 형식'과 '견적 데이터'를 참고하여 제안서를 작성하세요.
    출력은 반드시 JSON 형식이며, 키 이름은 {format_instructions}를 따르세요.

    [문서 형식]
    {doc_format}

    [작성 규칙]
    - 각 항목의 내용은 한국어 공식 문서체로 작성
    - 금액은 3자리 콤마로 표기
    - 'estimation_details_with_basis'는 각 견적의 작업명과 📌 계산 근거 요약만 포함
    - 계산 근거는 줄바꿈(\n)으로 구분된 bullet('- ') 형식
    - 'construction_period'는 작업 난이도, 면적, 인력 투입량 고려 예상 일수 작성

    [견적 데이터]
    {estimations}
    """)

    llm = ChatOpenAI(model_name="gpt-4o", temperature=0.3)
    prompt_with_schema = prompt.partial(
        format_instructions=parser.get_format_instructions(),
        doc_format=document_format
    )

    filled_proposal = (prompt_with_schema | llm | parser).invoke({"estimations": estimations_json})

    return filled_proposal
