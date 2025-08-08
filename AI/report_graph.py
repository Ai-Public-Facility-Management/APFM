from prioritize_facilities_node import prioritize_facilities_node
from generate_report_node import generate_report_node
from parse_report_node import parse_report_node
from draw_report_node import draw_report_node

from langgraph.graph import StateGraph
from typing import TypedDict, List, Dict, Any

class FacilityScoreDetail(TypedDict):
    damage_score: float
    hindrance_score: float
    complaint_score: float
    repair_score: float
    total: float

class Facility(TypedDict):
    name: str
    damage: str
    estimated_cost: int
    hindrance_level: str
    complaints: int
    last_repair_date: str
    priority_score: float
    score_detail: FacilityScoreDetail

class ReportState(TypedDict):
    inspection_date: str
    facilities: List[Facility]
    final_report: str
    parsed_sections: List[Dict[str, Any]]
    pdf_report_path: str

builder = StateGraph(ReportState)

builder.add_node("prioritize_facilities", prioritize_facilities_node)
builder.add_node("generate_report", generate_report_node)
builder.add_node("parse_report", parse_report_node)
builder.add_node("draw_report", draw_report_node)

# 흐름 연결
builder.set_entry_point("prioritize_facilities")
builder.add_edge("prioritize_facilities", "generate_report")
builder.add_edge("generate_report", "parse_report")
builder.add_edge("parse_report", "draw_report")
builder.set_finish_point("draw_report")

graph = builder.compile()