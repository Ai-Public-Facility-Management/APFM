# vision_high.py

from predict_high.analyze_high import analyze_facility_image  # 예: 함수가 다른 파일에 있다면 import

def vision_analysis_node(state: dict) -> dict:
    original_b64 = state["base64_image"]
    crop_b64 = state["crop_base64_image"]
    box = state["box"]

    # 분석 실행
    vision_result = analyze_facility_image(original_b64, crop_b64, box)

    # vision_result가 문자열이 아니라 dict 형태(status 포함)라고 가정
    state["vision_analysis"] = vision_result.get("vision_analysis")  # 비주얼 분석 설명
    state["status"] = vision_result.get("status")  # 상태 필드 추가
    return state