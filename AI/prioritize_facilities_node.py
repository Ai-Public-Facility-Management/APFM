from priority_score import priority_score
from report_state import ReportState

def prioritize_facilities_node(state: ReportState) -> ReportState:
    facilities = state.get("facilities", [])
    for facility in facilities:
        score_detail = priority_score(facility)
        facility["priority_score"] = score_detail["total"]
        facility["score_detail"] = score_detail

    prioritized = sorted(facilities, key=lambda x: x["priority_score"], reverse=True)
    state["facilities"] = prioritized
    return state