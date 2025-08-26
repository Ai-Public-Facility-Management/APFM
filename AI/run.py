from fastapi import FastAPI, HTTPException
from PIL import Image, ImageDraw
import io
import numpy as np
import base64
import torch
import os
import uuid
from dotenv import load_dotenv
from estimate_util import run_estimate
from proposal.generate import generate_proposal
from proposal.word import convert_to_word
from fastapi.responses import StreamingResponse
from fastapi.middleware.cors import CORSMiddleware
from routes import (predict_high, predict_board, predict_regular)  
from pydantic import BaseModel
from typing import List
import logging
from priority_graph import run_priority_graph

from model_config import (
    yolo_model, class_names, clip_device, clip_model,
    clip_preprocess, status_texts, status_tokens
)

load_dotenv()
app = FastAPI()

# CORS 설정
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 또는 ["http://localhost:3000"] 로 제한 가능
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

logger = logging.getLogger("uvicorn.error")

# 📌 /predict-high 라우터 등록
app.include_router(
    predict_high.router,
    prefix="",
    tags=["predict_high"]
)

app.include_router(predict_board.router)

app.include_router(predict_regular.router)


class FolderPathRequest(BaseModel):
    image_folder: str

# 기존 엔드포인트 유지
latest_proposal = None

@app.post("/ai/proposal/generate-from-spring")
def generate_from_spring(payload: dict):
    global latest_proposal
    proposal = generate_proposal(payload.get("estimations", []))
    latest_proposal = proposal
    return {"proposal": proposal}

@app.get("/ai/proposal/latest")
def get_latest_proposal():
    if not latest_proposal:
        raise HTTPException(status_code=404, detail="아직 생성된 제안서가 없습니다.")
    return {"proposal": latest_proposal}

@app.post("/ai/proposal-to-docx")
async def proposal_to_docx_api(request: dict):
    proposal_dict = request.get("proposal")
    if not proposal_dict:
        raise HTTPException(status_code=400, detail="proposal 정보 필요")

    docx_path = convert_to_word(proposal_dict)
    if not docx_path or not os.path.exists(docx_path):
        raise HTTPException(status_code=500, detail="docx 파일 생성 실패")
    with open(docx_path, "rb") as f:
        file_bytes = f.read()
    return StreamingResponse(
        io.BytesIO(file_bytes),
        media_type="application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        headers={"Content-Disposition": f"attachment; filename=proposal.docx"}
    )

class Facility(BaseModel):
    name: str
    damage: str
    estimated_cost: int
    hindrance_level: str
    complaints: int
    last_repair_date: str
    cost_basis: str = None
    priority_score: float = 0.0

class InspectionState(BaseModel):
    inspection_date: str
    facilities: List[Facility]

@app.post("/priority/run")
async def priority_run_api(state: InspectionState):
    try:
        state_dict = state.dict()
        result = run_priority_graph(state_dict)
        return {"data" : result["pdf_report_path"]}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"그래프 실행 실패: {str(e)}")

#uvicorn run:app --host 0.0.0.0 --port 8080 --reload