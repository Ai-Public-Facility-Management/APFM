from fastapi import APIRouter, HTTPException, UploadFile, File
import io, base64
from PIL import Image, ImageDraw
from model_config import (
    yolo_model, class_names, clip_device
)
from predict_high.estimate_util import run_estimate  # 고급 견적 실행 함수

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

# ---------- 라우트 ----------
@router.post("/predict-board")
async def predict_board(image: UploadFile = File(...)):
    try:
        # 원본 이미지 로드
        img = Image.open(image.file).convert('RGB')
        img_w, img_h = img.size
        b64_original = pil_to_base64(img)

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

        detection_results = []

        for idx, (box, score, cls_id) in enumerate(zip(boxes, scores, class_ids)):
            x1, y1, x2, y2 = map(int, box)
            x1e, y1e, x2e, y2e = expand_box(x1, y1, x2, y2, img_w, img_h)
            class_name = class_names[int(cls_id)]

            # 크롭 이미지 Base64 변환
            cropped = img.crop((x1e, y1e, x2e, y2e))
            b64_cropped = pil_to_base64(cropped)

            # ---- 고급 분석 & 견적 ----
            est_result = run_estimate(b64_original, b64_cropped, [x1e, y1e, x2e, y2e], "gpt-5-nano")

            detection_results.append({
                "class": class_name,
                "confidence": float(score),
                "box": [x1, y1, x2, y2],
                "expand_box": [x1e, y1e, x2e, y2e],
                "status": est_result.get("status"),
                "vision_analysis": est_result.get("vision_analysis"),
                "estimate": est_result.get("estimate"),
                "estimate_basis": est_result.get("estimate_basis")
            })

        return {"detections": detection_results}

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"이미지 처리 중 오류: {str(e)}")
