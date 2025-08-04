# vision.py

from analyze import analyze_facility_image  # 예: 함수가 다른 파일에 있다면 import

def vision_analysis_node(state: dict) -> dict:
    image_path = state["image_path"]
    vision_description = analyze_facility_image(image_path)
    state["vision_analysis"] = vision_description
    return state