from langgraph.graph import StateGraph
from typing import TypedDict
from vision import vision_analysis_node
from rag import rag_cost_estimate_node


# 상태 스키마
class EstimationState(TypedDict, total=False):
    image_path: str
    base64_image: str
    image_description: str
    cost_estimate: dict
    vision_analysis: str
    vectordb: object

# 그래프 정의
builder = StateGraph(EstimationState)

builder.add_node("vision_analysis", vision_analysis_node)
builder.add_node("rag_cost_estimate", rag_cost_estimate_node)

builder.set_entry_point("vision_analysis")
builder.add_edge("vision_analysis", "rag_cost_estimate")

graph = builder.compile()