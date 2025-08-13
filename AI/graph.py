from langgraph.graph import StateGraph
from typing import TypedDict, List, Any
from vision import vision_analysis_node
from rag import rag_cost_estimate_node

# 상태 스키마
class EstimationState(TypedDict, total=False):
    base64_image: str            # 전체 이미지 (base64)
    crop_base64_image: str       # 크롭 이미지 (base64)
    box: List[int]               # [x1, y1, x2, y2]
    image_description: str
    vision_analysis: str
    estimate: int                # 견적 금액 (정수)
    estimate_basis: str          # 계산 근거 + 참고 문서 요약
    vectordb: object

# 그래프 정의
builder = StateGraph(EstimationState)

builder.add_node("vision_analysis", vision_analysis_node)
builder.add_node("rag_cost_estimate", rag_cost_estimate_node)

builder.set_entry_point("vision_analysis")
builder.add_edge("vision_analysis", "rag_cost_estimate")

graph = builder.compile()
