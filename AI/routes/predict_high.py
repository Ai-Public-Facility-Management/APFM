# routes/predict_high.py
from fastapi import APIRouter, HTTPException
from pydantic import BaseModel
import os, uuid, io, base64
from PIL import Image, ImageDraw
from model_config import (
    yolo_model, class_names, clip_device, clip_model,
    clip_preprocess, status_texts, status_tokens
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

# ---------- 요청 모델 ----------
class FolderPathRequest(BaseModel):
    image_folder: str

# ---------- 라우트 ----------
@router.post("/predict-high")
async def predict_high(req: FolderPathRequest):
    image_folder = req.image_folder

    if not os.path.exists(image_folder) or not os.path.isdir(image_folder):
        raise HTTPException(status_code=400, detail="유효한 이미지 폴더 경로가 아닙니다.")

    valid_exts = {'.jpg', '.jpeg', '.png'}
    image_paths = sorted([
        os.path.join(image_folder, f)
        for f in os.listdir(image_folder)
        if os.path.splitext(f)[1].lower() in valid_exts
    ])

    if not image_paths:
        raise HTTPException(status_code=404, detail="폴더 내에 이미지 파일이 없습니다.")

    result_dict = {}

    for image_path in image_paths:
        camera_id = os.path.splitext(os.path.basename(image_path))[0]
        try:
            # 원본 이미지 로드
            img = Image.open(image_path).convert('RGB')
            img_w, img_h = img.size
            b64_original = pil_to_base64(img)

            # 저장 폴더 준비
            os.makedirs("temp/original", exist_ok=True)
            os.makedirs("temp/crops", exist_ok=True)
            unique_id = uuid.uuid4().hex

            # YOLO 추론
            results = yolo_model.predict(
                source=image_path,
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
                est_result = run_estimate(b64_original, b64_cropped, [x1e, y1e, x2e, y2e])

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
            result_dict[camera_id] = {"error": f"이미지 처리 중 오류: {str(e)}"}

    return result_dict
