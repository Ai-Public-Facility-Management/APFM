from prioritize_facilities_node import prioritize_facilities_node
from generate_report_node import generate_report_node
from parse_report_node import parse_report_node
from draw_report_node import draw_report_node

from langgraph.graph import StateGraph
from report_state import ReportState

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