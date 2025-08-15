from typing import List, Optional
from fastapi import FastAPI, Query
from pydantic import BaseModel, Field
import requests


class Estimation(BaseModel):
    vision_analysis: str
    estimate: int
    estimateBasis: str

class ClientRequest(BaseModel):
    estimations: List[Estimation]

def build_output_text(e: Estimation) -> str:
    return f"예상 견적 (원): {e.estimate:,}원\n\n📌 계산 근거 요약:\n{(e.estimateBasis or '').strip()}"


def generate_from_client(req: ClientRequest):
    items = [
        {
            "vision_analysis": e.vision_analysis,
            "output_text": build_output_text(e)
        }
        for e in req.estimations
    ]
    return {"estimations": items}  # ✅ 키 이름 수정 + 들여쓰기



# # ========== 1. 먼저 제안서 텍스트 생성 ==========
res = requests.get("http://localhost:8080/fetch-proposal")
data = res.json()  # dict 형태로 변환

resp = requests.post("http://localhost:8000/generate-proposal", json=data)
resp.raise_for_status()
proposal_json = resp.json()
print("=== 제안서 텍스트(JSON) ===")
print(proposal_json)