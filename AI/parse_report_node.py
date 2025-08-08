from parse_report_sections import parse_report_sections
from report_state import ReportState

def parse_report_node(state: ReportState) -> ReportState:
    report_text = state.get("final_report")
    if not report_text:
        raise ValueError("final_report가 state에 없습니다.")

    parsed = parse_report_sections(report_text)
    state["parsed_sections"] = parsed
    return state