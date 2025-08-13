# estimate_util.py
from langchain_community.vectorstores import Chroma
# 변경
from langchain_community.embeddings import HuggingFaceEmbeddings
from graph import graph  # graph.py에서 정의한 graph 객체

def run_estimate(original_b64: str, crop_b64: str, box: list):
    """
    image_path: str, 전체 이미지 파일 경로 (ex: cctv frame 저장 파일)
    box_coords: (x1, y1, x2, y2) 형태의 좌표 튜플/리스트 or dict
    """
    # 1. ChromaDB 불러오기
    embedding_model = HuggingFaceEmbeddings(model_name="jhgan/ko-sbert-nli")
    vectordb = Chroma(
        persist_directory="./chroma",  # chroma DB 경로
        embedding_function=embedding_model
    )

    # 2. 그래프 실행 (box 정보까지 함께 넘김)
    state = {
        "base64_image": original_b64,
        "crop_base64_image": crop_b64,
        "box": box,
        "vectordb": vectordb
    }
    result = graph.invoke(state)
    return {
        "vision_analysis": result.get("vision_analysis"),
        "estimate": result.get("estimate"),                 
        "estimate_basis": result.get("estimate_basis")  
    }
