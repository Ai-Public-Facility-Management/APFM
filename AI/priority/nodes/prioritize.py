# app/nodes/prioritize.py
from datetime import datetime

def priority_score(facility: dict) -> dict:
    detail = {}
    damage = facility.get("damage")

    if damage == "파손":
        detail["damage_score"] = 50
    elif damage in ["변형", "균열"]:
        detail["damage_score"] = 30
    else:
        detail["damage_score"] = 20

    hindrance_weight = {"없음": 0, "보통": 20, "높음": 40}
    detail["hindrance_score"] = hindrance_weight.get(facility.get("hindrance_level"), 0)

    complaints_score = min(int(facility.get("complaints", 0)) * 5, 30)
    detail["complaint_score"] = complaints_score

    try:
        last_date = datetime.strptime(facility.get("last_repair_date", ""), "%Y-%m-%d")
        days_since = (datetime.now() - last_date).days
        repair_score = min(days_since / 30, 20)
    except Exception:
        repair_score = 10
    detail["repair_score"] = round(repair_score, 1)

    detail["total"] = round(
        detail["damage_score"] + detail["hindrance_score"] + detail["complaint_score"] + detail["repair_score"], 1
    )
    return detail

def prioritize_facilities_node(state: dict) -> dict:
    facilities = state.get("facilities", [])
    for fac in facilities:
        sd = priority_score(fac)
        fac["score_detail"] = sd
        fac["priority_score"] = sd["total"]
    state["facilities"] = sorted(facilities, key=lambda x: x["priority_score"], reverse=True)
    return state
