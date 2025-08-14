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
        "vision_analysis": "...",   # LLM이 작성한 분석 텍스트
        "status": "파손"            # 상태 카테고리
    }
    """
    client = OpenAI(api_key=os.getenv("OPENAI_API_KEY"))

    x1, y1, x2, y2 = map(int, box)
    pixel_area = (x2 - x1) * (y2 - y1)

    prompt = (
        f"아래 두 이미지를 참고해 견적을 위한 설명을 해줘.\n"
        "- 첫 번째 이미지는 전체 CCTV 장면(주변환경 참고용)이야.\n"
        "- 두 번째 이미지는 박스 좌상단 ({x1}, {y1}), 우하단 ({x2}, {y2}) 좌표의 시설물만 크롭한 이미지야.\n\n"
        "두 번째 이미지만 보고 **시설물의 종류, 파손/이상 유형(파손, 변형, 벗겨짐, 균열 등), "
        "시설물의 대략적인 면적(픽셀/제곱미터 단위), 파손의 심각도 등 견적에 핵심적인 설명**을 해줘.\n"
        "첫 번째(전체) 이미지는 주변 환경이나 인접시설, 보행환경 상태, 장애물 등 '배경 설명'에만 참고해.\n\n"
        f"크롭 박스의 픽셀 면적은 {pixel_area}야.\n\n"
        f"추가로, 이 시설물의 상태를 반드시 다음 중 하나로 분류해줘: {', '.join(STATUS_CATEGORIES)}.\n\n"
        "출력은 반드시 JSON 형식으로 하고, 키는 다음과 같아야 해:\n"
        "{\n"
        "  \"vision_analysis\": \"텍스트 분석 내용\",\n"
        "  \"status\": \"상태 카테고리\"\n"
        "}\n"
        "절대 코드 블록(```json`)으로 감싸지 말고 순수 JSON만 출력해."
    )

    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[
            {
                "role": "system",
                "content": (
                    "너는 CCTV 시설물 파손/이상 판단 및 견적 설명 전문 AI야. "
                    "시설물 자체는 두 번째 이미지(크롭)만 근거로 설명하고, "
                    "첫 번째(전체) 이미지는 주변 설명에만 활용해. "
                    "반드시 순수 JSON 형식으로만 답변해."
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
        max_tokens=1000,
    )

    raw_content = response.choices[0].message.content.strip()

    # ✅ 코드 블록 제거
    cleaned = re.sub(r"^```json\s*|\s*```$", "", raw_content.strip(), flags=re.DOTALL).strip()

    try:
        parsed = json.loads(cleaned)
    except json.JSONDecodeError:
        parsed = {
            "vision_analysis": cleaned,
            "status": None
        }

    return parsed
