# routes/predict_regular.py
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
import os, uuid, io, base64
from PIL import Image, ImageDraw
import cv2
import re
from io import BytesIO
from typing import List
from pathlib import Path
from model_config import (
    yolo_model, class_names, clip_device
)
from predict_high.estimate_util import run_estimate
from tracking_module.tracking import track_multiple_facilities_analysis  # ← 방해도 분석 함수 import
router = APIRouter()

# ---------- 유틸 ----------
def pil_to_base64(pil_img):
    buf = io.BytesIO()
    pil_img.save(buf, format='PNG')
    return base64.b64encode(buf.getvalue()).decode('utf-8')

def expand_box(x1, y1, x2, y2, img_w, img_h, pad_px=40):
    return (
        max(0, x1 - pad_px),
        max(0, y1 - pad_px),
        min(img_w, x2 + pad_px),
        min(img_h, y2 + pad_px)
    )

def get_first_frame(video_path):
    """동영상에서 첫 번째 프레임을 PIL 이미지로 반환"""
    cap = cv2.VideoCapture(video_path)
    success, frame = cap.read()
    cap.release()
    if not success or frame is None:
        raise ValueError(f"첫 프레임을 읽을 수 없습니다: {video_path}")
    frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    return Image.fromarray(frame_rgb)

# # ---------- 요청 모델 ----------
# class FolderPathRequest(BaseModel):
#     camera_ids : List[int]

# ---------- 라우트 ----------
@router.post("/predict")
async def predict_regular_video():
    BASE_DIR = Path(__file__).parent.parent
    save_folder = BASE_DIR / "videos"
    if not os.path.exists(save_folder) or not os.path.isdir(save_folder):
        raise HTTPException(status_code=400, detail="유효한 동영상 폴더 경로가 아닙니다.")

    valid_exts = {'.mp4', '.avi', '.mov', '.mkv'}

    video_paths = sorted([
        os.path.join(save_folder, f)
        for f in os.listdir(save_folder)
        if os.path.splitext(f)[1].lower() in valid_exts
    ])

    if not video_paths:
        raise HTTPException(status_code=404, detail="폴더 내에 동영상 파일이 없습니다.")

    result_dict = []

    for video_path in video_paths:
        filename = os.path.splitext(os.path.basename(video_path))[0]
        match = re.search(r'\d+', filename)
        camera_id = int(match.group()) if match else None
        try:
            # 첫 프레임 추출
            img = get_first_frame(video_path)
            img_w, img_h = img.size
            b64_original = pil_to_base64(img)

            os.makedirs("temp/original", exist_ok=True)
            os.makedirs("temp/crops", exist_ok=True)
            unique_id = uuid.uuid4().hex

            # 첫 프레임에서 YOLO 시설물 탐지
            results = yolo_model.predict(
                source=img,
                save=False,
                imgsz=1024,
                conf=0.25,
                device=clip_device
            )[0]

            boxes = results.boxes.xyxy.cpu().numpy()
            scores = results.boxes.conf.cpu().numpy()
            class_ids = results.boxes.cls.cpu().numpy()

            img_with_boxes = img.copy()
            draw = ImageDraw.Draw(img_with_boxes)

            detection_results = []
            crop_paths = []
            facility_boxes_for_analysis = []

            for idx, (box, score, cls_id) in enumerate(zip(boxes, scores, class_ids)):
                x1, y1, x2, y2 = map(int, box)
                x1e, y1e, x2e, y2e = expand_box(x1, y1, x2, y2, img_w, img_h)
                class_name = class_names[int(cls_id)]

                # 시설물 박스 저장 (분석용)
                facility_boxes_for_analysis.append([x1, y1, x2, y2])

                # 박스 표시
                draw.rectangle([x1, y1, x2, y2], outline="red", width=8)
                draw.text((x1, y1 - 10), f"{class_name} {score:.2f}", fill="red")

                # 크롭 저장
                cropped = img.crop((x1e, y1e, x2e, y2e))
                # base64
                b64_cropped = pil_to_base64(cropped)

                # 견적 분석
                est_result = run_estimate(b64_original, b64_cropped, [x1e, y1e, x2e, y2e], "gpt-5")

                detection_results.append({
                    "publicFaType": class_name,
                    "box": [x1e, y1e, x2e, y2e],
                    "issueType": est_result.get("status"),
                    "vision_analysis": est_result.get("vision_analysis"),
                    "estimate": est_result.get("estimate"),
                    "estimate_basis": est_result.get("estimate_basis"),
                    "crop_image": b64_cropped,
                    "obstruction": None,           # 나중에 채움
                    "obstructionBasis": None       # 나중에 채움
                })

            # 방해도 분석 (영상 전체 한 번만 실행)
            if facility_boxes_for_analysis:
                obstruction_results = track_multiple_facilities_analysis(
                    video_path=video_path,
                    damaged_facility_boxes=facility_boxes_for_analysis
                )
                # 결과 매칭
                for det, obs in zip(detection_results, obstruction_results):
                    det["obstruction"] = obs["score"]
                    det["obstructionBasis"] = obs["analysis_text"]
            
            b64_camera = pil_to_base64(img_with_boxes)
                
            
            result_dict.append(
                {
                "camera_id": camera_id,
                "original_image": b64_camera,
                "detections": detection_results
            }
            )
            

        except Exception as e:
            result_dict.append(
                {
                "camera_id": camera_id,
                "original_image": "이미지 생성 실패",
                "detections": detection_results
            }
            )

    return result_dict
