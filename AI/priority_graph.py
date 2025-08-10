from langgraph.graph import StateGraph
from priority.nodes.prioritize import prioritize_facilities_node
from priority.nodes.generate_report import generate_report_node
from priority.nodes.parse_report import parse_report_node
from priority.nodes.draw_report import draw_report_node

from langchain.chat_models import ChatOpenAI


def build_graph(llm=None):
    builder = StateGraph(dict)

    builder.add_node("prioritize_facilities", prioritize_facilities_node)
    if llm:
        builder.add_node("generate_report", lambda s, _llm=llm: generate_report_node(s, llm=_llm))
    else:
        builder.add_node("generate_report", generate_report_node)
    builder.add_node("parse_report", parse_report_node)
    builder.add_node("draw_report", draw_report_node)

    builder.set_entry_point("prioritize_facilities")
    builder.add_edge("prioritize_facilities", "generate_report")
    builder.add_edge("generate_report", "parse_report")
    builder.add_edge("parse_report", "draw_report")
    builder.set_finish_point("draw_report")

    return builder.compile()

def run_priority_graph(state: dict, llm_name="gpt-4o"):
    llm = ChatOpenAI(model_name=llm_name, temperature=0)
    graph = build_graph(llm=llm)

    final_state = graph.invoke(state)

    return final_state