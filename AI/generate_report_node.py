report_prompt = """
당신은 공공시설물 정기점검 결과를 바탕으로 보고서를 작성하는 행정 담당자입니다. 아래는 점검된 시설물 목록이며, 우선순위는 이미 계산되어 정렬되어 있습니다.

각 시설물에 대해 다음 정보를 바탕으로 점검 결과를 서술하세요:
- 시설물 명칭
- 파손/노후화 상태
- 보행 방해 정도
- 민원 접수 횟수
- 마지막 수리일
- 예상 수리 견적
- 우선순위 점수
- 수리 견적 산출 근거

또한 전체 점검 결과를 종합해 다음 보고서 양식에 맞추어 전문을 작성하세요:
Ⅰ. 검토배경
 1. 공공시설물 관리를 위해 {inspection_date} 정기점검을 실행
 2. 이상이 발견된 공공시설물들
Ⅱ. 사안별 현황
 1. 우선순위 1번의 공공시설물에 대한 상태
 2. 우선순위 2번의 공공시설물에 대한 상태
 3. 우선순위 3번의 공공시설물에 대한 상태
Ⅲ. 검토 의견
 1. 사안별 현황 요약
 2. 예산 집행 제안
Ⅳ. 향후 조치 계획

예시:
Ⅰ. 검토배경
 1. 공공시설물 관리를 위해 날짜에 맞춰 정기점검을 실행
 2. 정류장 쉘터, 볼라드, 가로수 지주대에 대하여 이상 발견

Ⅱ. 사안별 현황
 1. 정류장 쉘터
  ㅇ 시설물 명칭: 정류장 쉘터
  ㅇ 파손 상태: 파손
  ㅇ 보행 방해 정도: 높음
  ㅇ 민원 접수 횟수: 5건
  ㅇ 마지막 수리일: 2022.09.15
  ㅇ 예상 수리 견적: 1,200,000원
  ㅇ 수리 견적 산출 근거 : 파손 부위 교체 및 보강 필요. 철재 구조물 보강 및 방풍 유리 교체 기준 적용. 특수 작업 인부 2명, 일반 인부 1명 기준. 일당 총합 450,000원으로 산정. 방풍 유리 교체 및 구조 보강용 자재 약 600,000원. 고소작업차 등 장비 임차 150.000원. 안전 펜스 설치 등 현장 안전조치 비용 100,000원.
  ㅇ 우선순위 점수: 135.0
 2. 우선순위 2번의 공공시설물에 대한 상태
 3. 우선순위 3번의 공공시설물에 대한 상태

Ⅲ. 검토 의견
 1. 사안별 현황 요약
 2. 예산 집행 제안

Ⅳ. 향후 조치 계획

이 때 문장은 명사로 끝날 경우 온점을 붙이지 않고 문장으로 끝나는 경우 온점을 붙이세요.
날짜를 적을 때에는 년, 월, 일을 생략하고 그 자리에 온점을 찍어 '2025.08.04'와 같이 나타냅니다.
불필요한 말은 덧붙이지 마세요. -, **, #등의 모든 기호를 제외하고 위의 보고서 양식만 준수하세요.
로마 숫자 뒤에 따르는 아라비아 숫자 항목은 소제목으로, 한 칸 띄우는 것이 원칙이고 항목과 항목 사이는 로마 숫자로 구분한 문단이 아니면 띄우지 않습니다.

시설물 리스트:
{facility_list}

보고서:
"""

from langchain_core.prompts import PromptTemplate
from langchain_openai import ChatOpenAI
from langchain.chains import LLMChain

def generate_report_node(state: dict) -> dict:
    facilities = state["facilities"]
    facility_text = "\n".join([
        f"- {f['name']} / {f['damage']} / 방해도: {f['hindrance_level']} / 민원: {f['complaints']}건 / 마지막 수리일: {f['last_repair_date']} / 견적: {f['estimated_cost']}원 / 우선순위 점수: {f['priority_score']:.1f} / 근거: {f.get('cost_basis', 'N/A')}"
        for f in facilities
    ])

    prompt = PromptTemplate.from_template(report_prompt)
    llm = ChatOpenAI(model="gpt-4o")
    chain = LLMChain(llm=llm, prompt=prompt)

    result = chain.invoke({
        "facility_list": facility_text,
        "inspection_date": state["inspection_date"].replace("-", ".")
    })

    state["final_report"] = result["text"]
    return state