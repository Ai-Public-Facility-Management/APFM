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
from routes import (predict_high, predict_board)  
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
logger = logging.getLogger("uvicorn.error")

def expand_box(x1, y1, x2, y2, img_w, img_h, pad_px=20):
    return (
        max(0, x1 - pad_px),
        max(0, y1 - pad_px),
        min(img_w, x2 + pad_px),
        min(img_h, y2 + pad_px)
    )

def pil_to_base64(pil_img, prefix=""):
    buf = io.BytesIO()
    pil_img.save(buf, format='JPEG')
    b64 = base64.b64encode(buf.getvalue()).decode('utf-8')
    return prefix + b64

# 📌 /predict-high 라우터 등록
app.include_router(
    predict_high.router,
    prefix="",
    tags=["predict_high"]
)

app.include_router(predict_board.router)

class ImagePathRequest(BaseModel):
    image_path: str

@app.post("/predict-img")
async def predict_path(req: ImagePathRequest):
    image_path = req.image_path
    if not os.path.exists(image_path):
        raise HTTPException(status_code=404, detail="Image file not found.")

    try:
        img = Image.open(image_path).convert('RGB')
        img_w, img_h = img.size
        b64_original = pil_to_base64(img)
        os.makedirs("temp/original", exist_ok=True)
        os.makedirs("temp/crops", exist_ok=True)
        unique_id = uuid.uuid4().hex

        results = yolo_model.predict(
            source=image_path,
            save=False,
            imgsz=1024,
            conf=0.25,
            device=clip_device
        )
        result = results[0]
        boxes = result.boxes.xyxy.cpu().numpy()
        scores = result.boxes.conf.cpu().numpy()
        class_ids = result.boxes.cls.cpu().numpy()

        img_with_boxes = img.copy()
        draw = ImageDraw.Draw(img_with_boxes)

        detection_results = []
        crop_paths = []

        for idx, (box, score, cls_id) in enumerate(zip(boxes, scores, class_ids)):
            x1, y1, x2, y2 = map(int, box)
            x1e, y1e, x2e, y2e = expand_box(x1, y1, x2, y2, img_w, img_h, pad_px=40)
            class_name = class_names[int(cls_id)]

            draw.rectangle([x1, y1, x2, y2], outline="red", width=3)
            draw.text((x1, max(0, y1 - 12)), f"{class_name} {score:.2f}", fill="red")

            cropped = img.crop((x1e, y1e, x2e, y2e))
            crop_filename = f"crop_{unique_id}_{idx}.jpg"
            crop_path = os.path.join("temp/crops", crop_filename)
            cropped.save(crop_path)
            crop_paths.append(crop_path)

            b64_cropped = pil_to_base64(cropped)

            clip_input = clip_preprocess(cropped).unsqueeze(0).to(clip_device)
            with torch.no_grad():
                logits_per_image, logits_per_text = clip_model(clip_input, status_tokens)
                probs = logits_per_image.softmax(dim=-1).cpu().numpy()[0]
                status_idx = int(np.argmax(probs))
                status = status_texts[status_idx]
                status_prob = float(probs[status_idx])

            vision_analysis = None
            estimate = None
            estimate_basis = None
            if status != "normal":
                try:
                    est_result = run_estimate(
                        b64_original,
                        b64_cropped,
                        [int(x1e), int(y1e), int(x2e), int(y2e)]
                    )
                    vision_analysis = est_result.get("vision_analysis")
                    estimate = est_result.get("estimate")
                    estimate_basis = est_result.get("estimate_basis")
                except Exception as e:
                    vision_analysis = f"Error: {str(e)}"

            detection_results.append({
                "class": class_name,
                "confidence": float(score),
                "box": [int(x1), int(y1), int(x2), int(y2)],
                "expand_box": [int(x1e), int(y1e), int(x2e), int(y2e)],
                "status": status,
                "status_conf": status_prob,
                "vision_analysis": vision_analysis,
                "estimate": estimate,
                "estimate_basis": estimate_basis,
                "crop_path": crop_path
            })

        boxed_image_path = f"temp/original/boxed_{unique_id}.jpg"
        img_with_boxes.save(boxed_image_path)

        return {
            "original_image_path": boxed_image_path,
            "crop_image_paths": crop_paths,
            "detections": detection_results
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Inference error: {str(e)}")

class FolderPathRequest(BaseModel):
    image_folder: str

@app.post("/predict")
async def batch_predict(req: FolderPathRequest):
    image_folder = req.image_folder
    if not os.path.exists(image_folder) or not os.path.isdir(image_folder):
        raise HTTPException(status_code=400, detail="유효한 이미지 폴더 경로가 아닙니다.")

    valid_exts = {'.jpg', '.jpeg', '.png'}
    files = [
        os.path.join(image_folder, f)
        for f in os.listdir(image_folder)
        if os.path.isfile(os.path.join(image_folder, f))
        and os.path.splitext(f)[1].lower() in valid_exts
    ]
    image_paths = sorted(files)
    if not image_paths:
        raise HTTPException(status_code=404, detail="폴더 내에 이미지 파일이 없습니다.")

    result_dict = {}

    for image_path in image_paths:
        camera_id = os.path.splitext(os.path.basename(image_path))[0]
        try:
            img = Image.open(image_path).convert('RGB')
            img_w, img_h = img.size
            b64_original = pil_to_base64(img)
            os.makedirs("temp/original", exist_ok=True)
            os.makedirs("temp/crops", exist_ok=True)
            unique_id = uuid.uuid4().hex

            results = yolo_model.predict(
                source=image_path,
                save=False,
                imgsz=1024,
                conf=0.25,
                device=clip_device
            )
            result = results[0]
            boxes = result.boxes.xyxy.cpu().numpy()
            scores = result.boxes.conf.cpu().numpy()
            class_ids = result.boxes.cls.cpu().numpy()

            img_with_boxes = img.copy()
            draw = ImageDraw.Draw(img_with_boxes)

            detection_results = []
            crop_paths = []

            for idx, (box, score, cls_id) in enumerate(zip(boxes, scores, class_ids)):
                x1, y1, x2, y2 = map(int, box)
                x1e, y1e, x2e, y2e = expand_box(x1, y1, x2, y2, img_w, img_h, pad_px=40)
                class_name = class_names[int(cls_id)]

                draw.rectangle([x1, y1, x2, y2], outline="red", width=5)
                draw.text((x1, y1 - 10), f"{class_name} {score:.2f}", fill="red")

                cropped = img.crop((x1e, y1e, x2e, y2e))
                crop_filename = f"crop_{camera_id}_{unique_id}_{idx}.jpg"
                crop_path = os.path.join("temp/crops", crop_filename)
                cropped.save(crop_path)
                crop_paths.append(crop_path)

                b64_cropped = pil_to_base64(cropped)

                clip_input = clip_preprocess(cropped).unsqueeze(0).to(clip_device)
                with torch.no_grad():
                    logits_per_image, logits_per_text = clip_model(clip_input, status_tokens)
                    probs = logits_per_image.softmax(dim=-1).cpu().numpy()[0]
                    status_idx = int(np.argmax(probs))
                    status = status_texts[status_idx]
                    status_prob = float(probs[status_idx])

                vision_analysis = None
                estimate = None
                estimate_basis = None
                obstruction = None
                obstructionBasis = None
                if status != "normal":
                    try:
                        est_result = run_estimate(
                            b64_original,
                            b64_cropped,
                            [int(x1e), int(y1e), int(x2e), int(y2e)]
                        )
                        vision_analysis = est_result.get("vision_analysis")
                        estimate = est_result.get("estimate")
                        estimate_basis = est_result.get("estimate_basis")
                    except Exception as e:
                        vision_analysis = f"Error: {str(e)}"

                detection_results.append({
                    "class": class_name,
                    "confidence": float(score),
                    "box": [int(x1), int(y1), int(x2), int(y2)],
                    "expand_box": [int(x1e), int(y1e), int(x2e), int(y2e)],
                    "status": status,
                    "status_conf": status_prob,
                    "vision_analysis": vision_analysis,
                    "estimate": estimate,
                    "estimate_basis": estimate_basis,
                    "crop_path": crop_path,
                    "obstruction": obstruction,
                    "obstructionBasis": obstructionBasis
                })

            boxed_image_path = f"temp/original/boxed_{camera_id}_{unique_id}.jpg"
            img_with_boxes.save(boxed_image_path)

            result_dict[camera_id] = {
                "camera_id": camera_id,
                "original_image_path": boxed_image_path,
                "crop_image_paths": crop_paths,
                "detections": detection_results
            }

        except Exception as e:
            result_dict[camera_id] = {"error": f"이미지 처리 중 오류: {str(e)}"}

    return result_dict

# 기존 엔드포인트 유지
@app.post("/generate-proposal")
async def generate_proposal_api(request: dict):
    estimations = request.get("estimations", [])
    proposal = generate_proposal(estimations)
    return {"proposal": proposal}

@app.post("/proposal-to-docx")
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
        return result
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"그래프 실행 실패: {str(e)}")
#uvicorn run:app --host 0.0.0.0 --port 8080 --reload