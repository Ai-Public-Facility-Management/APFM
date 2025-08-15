import base64
import os
import json
import re
from openai import OpenAI

STATUS_CATEGORIES = ['정상', '표면 벗겨짐', '파손', '변형', '변색', '균열']

def analyze_facility_image(original_b64: str, crop_b64: str, box: list) -> dict:
    """
    original_b64: 전체 CCTV 이미지 (base64)
    crop_b64: 시설물 크롭 이미지 (base64)
    box: [x1, y1, x2, y2]
    return: {
        "vision_analysis": "...",   # LLM이 작성한 분석 텍스트(문자열 하나)
        "status": "파손"            # 상태 카테고리
    }
    """
    client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

    x1, y1, x2, y2 = map(int, box)
    pixel_area = (x2 - x1) * (y2 - y1)

    # ---- 강화된 user 프롬프트 (vision_analysis를 문자열로 강제) ----
    prompt = (
        "아래 두 이미지를 참고해 **견적 산출에 바로 쓸 수 있는** 상세 분석을 작성하라.\n"
        "규칙(아주 중요):\n"
        "• `vision_analysis`는 **문자열 하나**만 허용한다. 문자열 내부에 JSON/배열/객체/키:값/[]/{} 를 절대 쓰지 마라.\n"
        "• 줄바꿈은 허용. 문단/목록은 하이픈(-)으로 표현해라. 따옴표 이스케이프(\\\")는 최소화.\n"
        f"• `status`는 다음 중 하나만: {', '.join(STATUS_CATEGORIES)}.\n"
        "• 최종 출력은 **순수 JSON**: {\"vision_analysis\": \"...\", \"status\": \"...\"}.\n"
        "\n"
        "분석 지침:\n"
        "1) 시설물 판정(종류/손상/크기 등)은 두 번째 이미지(크롭)만 근거로 한다.\n"
        "2) 첫 번째(전체) 이미지는 주변환경·시공제약·안전·교통 영향 등 배경 설명에만 활용한다.\n"
        "3) 모든 수치는 단위를 포함, 면적은 **px²**와 **m²** 모두 제시.\n"
        "4) 실제 길이/면적(m, m²) 추정: 전체 이미지의 **표준 참조물**(보도블록≈0.3m, 차선폭≈0.15m 라인/차로폭 3.0~3.5m 등)로 px↔m 스케일 산정. "
        "불명확하면 **보수적 범위**로 제시하고 스케일 산정 근거를 짧게 명시.\n"
        "5) 불확실성은 범위(최소~최대) 또는 신뢰도(0~1)로 표기.\n"
        "\n"
        f"- 크롭 박스 좌상단=({x1}, {y1}), 우하단=({x2}, {y2}), 크롭 픽셀 면적≈{pixel_area}px².\n"
        "\n"
        "vision_analysis는 아래 **문자열 서식**을 그대로 포함해 값만 채워라(한글 라벨 유지, 내부에 JSON/배열 금지):\n"
        "[시설물 요약]\n"
        "- 종류(추정): …\n"
        "- 재질(추정): … (예: 금속/철제, 플라스틱, 고무, 콘크리트 등)\n"
        "- 규격/크기(추정): 가로 …px × 세로 …px (스케일 적용 시 …m × …m)\n"
        "- 시설물 면적: …px² (스케일 적용 시 …m², 근거: …)\n"
        "- 손상 유형: … (예: 파손/변형/표면 벗겨짐/변색/균열 등)\n"
        "- 손상 범위: 가로 …px × 세로 …px (≈ …px²) → 스케일 …m × …m (≈ …m²)\n"
        "- 심각도: 경미/보통/중대 중 하나 + 근거(구부러짐 각도, 결손 길이, 균열 폭 등 수치화)\n"
        "\n"
        "[주변 환경/시공 고려]\n"
        "- 인접 시설물/장애물: … (거리 추정 …px → …m)\n"
        "- 보행/차량 동선 영향: … (임시 통제 필요 여부, 보행자 우회 동선 길이 추정 …m)\n"
        "- 접근성/시공 제약: 장비 진입, 야간 시공 필요성, 지중 매설물 가능성, 소음/진동 규제 등 …\n"
        "- 안전조치 권고: 코닝/바리케이드, 작업인원, 신호수 배치 등 …\n"
        "\n"
        "[요약(2줄)]\n"
        "첫째 줄: 현재 시설물 상태 핵심 요약(종류/재질·손상유형·심각도·대표 수치 한두 개 포함) 한 문장으로 서술.\n"
        "둘째 줄: 주변 환경 및 시공 고려(보행/차량 영향·접근성·통제 필요성 등) 핵심만 한 문장으로 서술.\n"
        "\n"
        "반드시 다음 형식의 **순수 JSON**만 출력하라 (코드블록 금지):\n"
        "{\"vision_analysis\": \"<위 서식을 채운 하나의 긴 문자열>\", \"status\": \"<카테고리>\"}"
    )

    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[
            {
                "role": "system",
                "content": (
                    "너는 CCTV 기반 공공시설물 상태판정·견적설명 전문가 AI다. "
                    "반드시 **순수 JSON**만 출력하고, 키는 정확히 `vision_analysis`(문자열)와 `status`(문자열) 두 개만 사용한다. "
                    "`vision_analysis`는 **오직 문자열 하나**로만 작성하며, 내부에 JSON/배열/객체/키:값 구조/대괄호·중괄호를 절대 넣지 마라. "
                    "줄바꿈은 허용되지만, 따옴표 이스케이프(\\\")를 최소화하고 자연스러운 한국어 문장으로 쓴다. "
                    "시설물의 유형·재질·규격/크기·손상유형·손상범위·심각도·면적(px²와 m²)·시공/안전/교통통제 고려사항을 정량적으로 기술하라. "
                    "전체 이미지는 주변환경(보행/차량 동선, 인접시설, 진입로/장비 접근성 등) 설명에만 활용하고, 시설물 자체 판정은 크롭 이미지 근거로만 판단하라. "
                    "불확실성은 추정치 범위(예: 0.12~0.18m²)와 근거를 함께 제시하라. "
                    "수치에는 반드시 단위를 붙이고, 판단 근거(시각적 단서)를 명시하라. "
                    "status 값은 제공된 카테고리 중 하나만 선택한다. "
                    "코드블록 금지."
                ),
            },
            {
                "role": "user",
                "content": [
                    {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{original_b64}" }},
                    {"type": "image_url", "image_url": {"url": f"data:image/jpeg;base64,{crop_b64}" }},
                    {"type": "text", "text": prompt}
                ],
            },
        ],
        max_tokens=1200,
    )

    raw_content = response.choices[0].message.content.strip()

    # 코드블록 제거
    cleaned = re.sub(r"^```json\s*|\s*```$", "", raw_content.strip(), flags=re.DOTALL).strip()

    # JSON 파싱 + 안전망
    try:
        parsed = json.loads(cleaned)
    except json.JSONDecodeError:
        parsed = {"vision_analysis": cleaned, "status": None}

    # ✅ vision_analysis가 문자열이 아닐 경우 문자열화
    if not isinstance(parsed.get("vision_analysis"), str):
        parsed["vision_analysis"] = json.dumps(parsed.get("vision_analysis"), ensure_ascii=False)

    # ✅ status가 허용 카테고리 밖이면 None 처리(선택: 후처리 매핑 규칙 추가 가능)
    if parsed.get("status") not in STATUS_CATEGORIES:
        parsed["status"] = parsed.get("status") if parsed.get("status") in STATUS_CATEGORIES else None

    return parsed
