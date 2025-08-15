# estimate_util.py
from langchain_community.vectorstores import Chroma
from langchain.embeddings import HuggingFaceEmbeddings
from predict_high.graph_high import graph  # graph.py에서 정의한 graph 객체

from model_config import embedding_model, vectordb  


def run_estimate(original_b64: str, crop_b64: str, box: list, model: str):
    """
    original_b64: 전체 이미지 (base64)
    crop_b64: 크롭 이미지 (base64)
    box: [x1, y1, x2, y2]
    """

    # 2. 그래프 실행
    state = {
        "base64_image": original_b64,
        "crop_base64_image": crop_b64,
        "box": box,
        "vectordb": vectordb,
        "model_name": model
    }

    result = graph.invoke(state)

    # 3. 결과 반환 (status 포함)
    return {
        "status": result.get("status"),                   # 정상 / 표면 벗겨짐 / 파손 등
        "vision_analysis": result.get("vision_analysis"),
        "estimate": result.get("estimate"),                # 정상일 경우 None
        "estimate_basis": result.get("estimate_basis")     # 정상일 경우 None
    }
