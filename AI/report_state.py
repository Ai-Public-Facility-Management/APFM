# report_state.py
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
