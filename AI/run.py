from fastapi import FastAPI, File, UploadFile, HTTPException, Form
from ultralytics import YOLO
from PIL import Image
import io
import numpy as np
import base64
import torch
import clip
import os
import shutil
import uuid
from dotenv import load_dotenv
from estimate_util import run_estimate  # 별도 정의한 견적 함수
from proposal.generate import generate_proposal
from proposal.word import convert_to_word
from fastapi.responses import StreamingResponse

load_dotenv()
app = FastAPI()

# YOLO 모델 (global)
yolo_model = YOLO('weights/yolo/v8/best.pt')
class_names = [
    "Bench", "BenchBack", "Pagora", "Trench", "PavementBlock", "ConstructionCover", "StreetTreeCover",
    "RoadSafetySign", 'BoundaryStone', 'BrailleBlock', 'TreeSupport', 'FlowerStand', 'StreetLampPole',
    'SignalController', 'Manhole', 'WalkAcrossPreventionFacility', 'SoundproofWalls', 'ProtectionFence',
    'Bollard', 'TelephoneBooth', 'DirectionalSign', 'PostBox', 'BicycleRack', 'TrashCan', 'StationShelter', 'StationSign'
]

# CLIP 모델 (global)
clip_device = "cuda" if torch.cuda.is_available() else "cpu"
clip_model, clip_preprocess = clip.load("ViT-B/32", device=clip_device)
status_texts = ["normal", "surface peeling", "damage", "frature", "distortion"]
status_tokens = clip.tokenize(status_texts).to(clip_device)

def expand_box(x1, y1, x2, y2, img_w, img_h, pad_px=20):
    return (
        max(0, x1 - pad_px),
        max(0, y1 - pad_px),
        min(img_w, x2 + pad_px),
        min(img_h, y2 + pad_px)
    )

def save_upload_file(upload_file: UploadFile, folder="temp"):
    os.makedirs(folder, exist_ok=True)
    file_ext = os.path.splitext(upload_file.filename)[-1]
    unique_name = f"{uuid.uuid4().hex}{file_ext}"
    file_path = os.path.join(folder, unique_name)
    with open(file_path, "wb") as buffer:
        shutil.copyfileobj(upload_file.file, buffer)
    return file_path

def remove_file_safely(path):
    try:
        os.remove(path)
    except Exception:
        pass

def pil_to_base64(pil_img, prefix=""):
    """PIL 이미지 → base64 인코딩 문자열 (prefix: 'data:image/jpeg;base64,' 자동 추가 가능)"""
    buf = io.BytesIO()
    pil_img.save(buf, format='JPEG')
    b64 = base64.b64encode(buf.getvalue()).decode('utf-8')
    return prefix + b64

@app.post("/predict")
async def predict(image: UploadFile = File(...), email: str = Form(...)):
    image_path = save_upload_file(image, folder="temp")
    try:
        img = Image.open(image_path).convert('RGB')
        img_w, img_h = img.size

        # 원본 이미지를 미리 base64로 인코딩 (한번만)
        b64_original = pil_to_base64(img, prefix="")

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

        detection_results = []
        crop_images = []
        for idx, (box, score, cls_id) in enumerate(zip(boxes, scores, class_ids)):
            x1, y1, x2, y2 = map(int, box)
            x1e, y1e, x2e, y2e = expand_box(x1, y1, x2, y2, img_w, img_h, pad_px=40)
            class_name = class_names[int(cls_id)]

            # 크롭 생성 및 CLIP 분석
            cropped = img.crop((x1e, y1e, x2e, y2e))
            clip_input = clip_preprocess(cropped).unsqueeze(0).to(clip_device)
            with torch.no_grad():
                logits_per_image, logits_per_text = clip_model(clip_input, status_tokens)
                probs = logits_per_image.softmax(dim=-1).cpu().numpy()[0]
                status_idx = int(np.argmax(probs))
                status = status_texts[status_idx]
                status_prob = float(probs[status_idx])

            # crop 이미지 base64 인코딩 (반환/분석 모두에서 재활용)
            b64_cropped = pil_to_base64(cropped, prefix="")

            vision_analysis = None
            cost_estimate = None
            if status != "normal":
                try:
                    result = run_estimate(
                        b64_original,      # 원본 전체 base64
                        b64_cropped,       # 크롭 영역 base64
                        [int(x1e), int(y1e), int(x2e), int(y2e)]  # 확장 박스 좌표
                    )
                    vision_analysis = result.get("vision_analysis")
                    cost_estimate = result.get("cost_estimate")
                except Exception as e:
                    vision_analysis = f"Error: {str(e)}"
                    cost_estimate = None

            detection_results.append({
                "class": class_name, #시설물 이름
                "confidence": float(score), #시설물 확률
                "box": [int(x1), int(y1), int(x2), int(y2)], #시설물 이미지 좌표
                "expand_box": [int(x1e), int(y1e), int(x2e), int(y2e)], #주변 시설물 확장 좌표
                "status": status, #상태
                "status_conf": status_prob, #상태 확률
                "vision_analysis": vision_analysis, #영상 이미지 분석
                "cost_estimate": cost_estimate #견적 산정 및 근거
            })
            # base64에 prefix 붙여서 프론트에서 바로 써도 됨 (여기선 prefix 안 붙임)
            crop_images.append(b64_cropped)

        return {
            "detections": detection_results,
            "crops": crop_images,
            "original_image": b64_original
        }
    finally:
        remove_file_safely(image_path)

@app.post("/generate-proposal")
async def generate_proposal_api(request: dict):
    """
    요청 예시:
    {
        "estimations": [...]
    }
    """
    estimations = request.get("estimations", [])
    proposal = generate_proposal(estimations)  # 딕트 하나 반환
    return {"proposal": proposal}

@app.post("/proposal-to-docx")
async def proposal_to_docx_api(request: dict):
    """
    요청 예시:
    {
        "proposal": { ... }  # generate_proposal의 출력값 그대로
    }
    """
    proposal_dict = request.get("proposal")
    if not proposal_dict:
        raise HTTPException(status_code=400, detail="proposal 정보 필요")

    # 실제 docx 생성
    docx_path = convert_to_word(proposal_dict)  # 경로 반환

    if not docx_path or not os.path.exists(docx_path):
        raise HTTPException(status_code=500, detail="docx 파일 생성 실패")
    with open(docx_path, "rb") as f:
        file_bytes = f.read()
    return StreamingResponse(
        io.BytesIO(file_bytes),
        media_type="application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        headers={"Content-Disposition": f"attachment; filename=proposal.docx"}
    )

# 실행:
# uvicorn run:app --host 0.0.0.0 --port 8080 --reload
