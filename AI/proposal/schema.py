from typing import List
from pydantic import BaseModel
from langchain_core.output_parsers import JsonOutputParser

# ========== Proposal Template ==========
class ProposalTemplate(BaseModel):
    project_name: str
    project_overview: str
    construction_period: str
    site_analysis_summary: List[str]
    estimation_details_with_basis: List[str]
    total_cost: str
    manpower_plan: str
    equipment_plan: str
    safety_quality_plan: str
    conclusion_expected_effect: str

parser = JsonOutputParser(pydantic_object=ProposalTemplate)
