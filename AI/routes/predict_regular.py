# routes/predict_high.py
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
import os, uuid, io, base64
from PIL import Image, ImageDraw
import cv2
from model_config import (
    yolo_model, class_names, clip_device,
    status_texts, status_tokens
)
from predict_high.estimate_util import run_estimate    # 고급 견적 실행 함수

router = APIRouter()

# ---------- 유틸 ----------
def pil_to_base64(pil_img):
    buf = io.BytesIO()
    pil_img.save(buf, format='JPEG')
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
    # BGR → RGB 변환
    frame_rgb = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
    return Image.fromarray(frame_rgb)

# ---------- 요청 모델 ----------
class FolderPathRequest(BaseModel):
    video_folder: str

# ---------- 라우트 ----------
@router.post("/predict-regular-video")
async def predict_regular_video(req: FolderPathRequest):
    video_folder = req.video_folder

    if not os.path.exists(video_folder) or not os.path.isdir(video_folder):
        raise HTTPException(status_code=400, detail="유효한 동영상 폴더 경로가 아닙니다.")

    # 동영상 확장자 목록
    valid_exts = {'.mp4', '.avi', '.mov', '.mkv'}
    video_paths = sorted([
        os.path.join(video_folder, f)
        for f in os.listdir(video_folder)
        if os.path.splitext(f)[1].lower() in valid_exts
    ])

    if not video_paths:
        raise HTTPException(status_code=404, detail="폴더 내에 동영상 파일이 없습니다.")

    result_dict = {}

    for video_path in video_paths:
        camera_id = os.path.splitext(os.path.basename(video_path))[0]
        try:
            # 첫 프레임 추출
            img = get_first_frame(video_path)
            img_w, img_h = img.size
            b64_original = pil_to_base64(img)

            # 저장 폴더 준비
            os.makedirs("temp/original", exist_ok=True)
            os.makedirs("temp/crops", exist_ok=True)
            unique_id = uuid.uuid4().hex

            # YOLO 추론
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

            for idx, (box, score, cls_id) in enumerate(zip(boxes, scores, class_ids)):
                x1, y1, x2, y2 = map(int, box)
                x1e, y1e, x2e, y2e = expand_box(x1, y1, x2, y2, img_w, img_h)
                class_name = class_names[int(cls_id)]

                # 박스 표시
                draw.rectangle([x1, y1, x2, y2], outline="red", width=8)
                draw.text((x1, y1 - 10), f"{class_name} {score:.2f}", fill="red")

                # 크롭 이미지 저장
                cropped = img.crop((x1e, y1e, x2e, y2e))
                crop_filename = f"crop_{camera_id}_{unique_id}_{idx}.jpg"
                crop_path = os.path.join("temp/crops", crop_filename)
                cropped.save(crop_path)
                crop_paths.append(crop_path)

                b64_cropped = pil_to_base64(cropped)

                # ---- 고급 분석 & 견적 ----
                est_result = run_estimate(b64_original, b64_cropped, [x1e, y1e, x2e, y2e], "gpt-5")

                detection_results.append({
                    "class": class_name,
                    "confidence": float(score),
                    "box": [x1, y1, x2, y2],
                    "expand_box": [x1e, y1e, x2e, y2e],
                    "status": est_result.get("status"),
                    "vision_analysis": est_result.get("vision_analysis"),
                    "estimate": est_result.get("estimate"),
                    "estimate_basis": est_result.get("estimate_basis"),
                    "crop_path": crop_path
                })

            # 박스가 그려진 원본 이미지 저장
            boxed_image_path = f"temp/original/boxed_{camera_id}_{unique_id}.jpg"
            img_with_boxes.save(boxed_image_path)

            result_dict[camera_id] = {
                "camera_id": camera_id,
                "original_image_path": boxed_image_path,
                "crop_image_paths": crop_paths,
                "detections": detection_results
            }

        except Exception as e:
            result_dict[camera_id] = {"error": f"동영상 처리 중 오류: {str(e)}"}

    return result_dict
