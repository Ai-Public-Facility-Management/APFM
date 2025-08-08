from datetime import datetime
from typing import Dict

def priority_score(facility: Dict) -> Dict:
    detail = {}

    # 1. damage 상태
    if facility["damage"] == "파손":
        detail["damage_score"] = 50
    elif facility["damage"] == "노후화":
        detail["damage_score"] = 30
    else:
        detail["damage_score"] = 0

    # 2. hindrance_level
    hindrance_weight = {"없음": 0, "보통": 20, "높음": 40}
    detail["hindrance_score"] = hindrance_weight.get(facility["hindrance_level"], 0)

    # 3. 민원 접수 이력
    complaints_score = min(facility.get("complaints", 0) * 5, 30)
    detail["complaint_score"] = complaints_score

    # 4. 마지막 수리 날짜
    try:
        last_date = datetime.strptime(facility["last_repair_date"], "%Y-%m-%d")
        days_since = (datetime.now() - last_date).days
        repair_score = min(days_since / 30, 20)  # 매 30일마다 1점, 최대 20점
    except:
        repair_score = 10  # 날짜 오류 시 기본값
    detail["repair_score"] = round(repair_score, 1)

    # 총점
    detail["total"] = round(
        detail["damage_score"] + detail["hindrance_score"] + detail["complaint_score"] + detail["repair_score"], 1
    )

    return detail