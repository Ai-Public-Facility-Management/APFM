from langgraph.graph import StateGraph
from typing import TypedDict, List
from predict_high.vision_high import vision_analysis_node
from rag import rag_cost_estimate_node

class EstimationState(TypedDict, total=False):
    base64_image: str
    crop_base64_image: str
    box: List[int]
    image_description: str
    vision_analysis: str
    status: str
    estimate: int
    estimate_basis: str
    vectordb: object

builder = StateGraph(EstimationState)

builder.add_node("vision_analysis", vision_analysis_node)
builder.add_node("rag_cost_estimate", rag_cost_estimate_node)

def end_node(state: EstimationState):
    return state

builder.add_node("end", end_node)

builder.set_entry_point("vision_analysis")

def route_from_vision(state: EstimationState):
    if state.get("status") == "정상":
        state["estimate"] = None
        state["estimate_basis"] = ""  # 안전하게 빈 문자열
        return "end"
    return "rag_cost_estimate"

builder.add_conditional_edges(
    "vision_analysis",
    route_from_vision,
    {
        "rag_cost_estimate": "rag_cost_estimate",
        "end": "end"
    }
)

graph = builder.compile()
