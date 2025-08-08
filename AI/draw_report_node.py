from draw_report import draw_report
from report_state import ReportState


def draw_report_node(state: ReportState) -> ReportState:
    parsed_sections = state.get("parsed_sections")
    inspection_date = state.get("inspection_date")
    facilities = state.get("facilities")

    output_path = "정기점검보고서.pdf"

    # draw_report 함수 호출
    draw_report(parsed_sections, inspection_date, facilities, output=output_path)

    # PDF 경로를 state에 저장해서 다음 노드에서 활용 가능
    state["pdf_report_path"] = output_path
    return state