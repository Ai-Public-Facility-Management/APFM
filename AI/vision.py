# vision.py

from analyze import analyze_facility_image  # 예: 함수가 다른 파일에 있다면 import

def vision_analysis_node(state: dict) -> dict:
    original_b64 = state["base64_image"]
    crop_b64 = state["crop_base64_image"]
    box = state["box"]
    # (아래 함수는 직접 정의)
    vision_result = analyze_facility_image(original_b64, crop_b64, box)
    state["vision_analysis"] = vision_result
    return state