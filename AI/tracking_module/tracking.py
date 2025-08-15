from ultralytics import YOLO
import cv2
import numpy as np
from torchvision.ops import box_iou
import torch
import math
from model_config import (tracking_model, clip_device)

def calculate_angle(v1, v2):
    """벡터 각도 계산"""
    dot = v1[0]*v2[0] + v1[1]*v2[1]
    mag1 = math.sqrt(v1[0]**2 + v1[1]**2)
    mag2 = math.sqrt(v2[0]**2 + v2[1]**2)
    if mag1 == 0 or mag2 == 0:
        return 0
    cos_theta = dot / (mag1 * mag2)
    return math.degrees(math.acos(np.clip(cos_theta, -1.0, 1.0)))

def track_multiple_facilities_analysis(
    video_path,
    damaged_facility_boxes,   # [[x1,y1,x2,y2], ...]
    iou_threshold=0.05,
    size_ratio_max=10,
    direction_change_angle=30
):
    """
    CCTV 영상 1회 분석으로 여러 시설물 방해도 계산
    """
    class_names = [
        "wheelchair", "truck", "tree_trunk", "traffic_sign", "traffic_light", "table",
        "stroller", "stop", "scooter", "potted_plant", "pole", "person", "parking_meter",
        "movable_signage", "motorcycle", "kiosk", "fire_hydrant", "dog", "chair", "cat",
        "carrier", "car", "bus", "bollard", "bicycle", "bench", "barricade"
    ]
    person_cls_id = class_names.index("person")

    cap = cv2.VideoCapture(video_path)
    fps = cap.get(cv2.CAP_PROP_FPS)
    total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    total_video_time = total_frames / fps if fps > 0 else 0

    # 시설물별 통계 초기화
    facility_stats = []
    for _ in damaged_facility_boxes:
        facility_stats.append({
            "overlap_ids": set(),
            "overlap_time_per_id": {},
            "track_history": {},
            "direction_change_ids": set()
        })

    unique_ids = set()

    while cap.isOpened():
        ret, frame = cap.read()
        if not ret:
            break

        results = tracking_model.track(
            source=frame,
            imgsz=1024,
            conf=0.25,
            tracker='bytetrack.yaml',
            persist=True,
            verbose=False,
            device=clip_device
        )

        if results[0].boxes.id is None:
            continue

        boxes = results[0].boxes.xyxy.cpu().numpy()
        ids = results[0].boxes.id.cpu().numpy().astype(int)
        class_ids = results[0].boxes.cls.cpu().numpy()

        # 사람 필터링
        person_boxes = []
        person_ids = []
        for box, tid, cid in zip(boxes, ids, class_ids):
            if int(cid) == person_cls_id:
                unique_ids.add(tid)
                person_boxes.append(box)
                person_ids.append(tid)

        if not person_boxes:
            continue

        # IoU 계산 (시설물 × 사람)
        facility_t = torch.tensor(damaged_facility_boxes, dtype=torch.float32)
        person_t = torch.tensor(person_boxes, dtype=torch.float32)
        ious = box_iou(facility_t, person_t)

        for p_idx, p_box in enumerate(person_boxes):
            tid = person_ids[p_idx]
            # 각 시설물과 비교
            for f_idx, iou_val in enumerate(ious[:, p_idx]):
                f_box = damaged_facility_boxes[f_idx]
                f_area = (f_box[2] - f_box[0]) * (f_box[3] - f_box[1])
                p_area = (p_box[2] - p_box[0]) * (p_box[3] - p_box[1])
                size_ratio = max(f_area / p_area, p_area / f_area)

                # 겹침 조건 만족 시
                if iou_val >= iou_threshold and size_ratio <= size_ratio_max:
                    fs = facility_stats[f_idx]
                    fs["overlap_ids"].add(tid)
                    fs["overlap_time_per_id"][tid] = fs["overlap_time_per_id"].get(tid, 0) + (1 / fps)

                # 이동 경로 기록
                cx = (p_box[0] + p_box[2]) / 2
                cy = (p_box[1] + p_box[3]) / 2
                facility_stats[f_idx]["track_history"].setdefault(tid, []).append((cx, cy))

    cap.release()

    # 최종 계산
    results = []
    total_person_count = len(unique_ids)

    for f_idx, stats in enumerate(facility_stats):
        avg_overlap_time = np.mean(list(stats["overlap_time_per_id"].values())) if stats["overlap_time_per_id"] else 0

        # 방향 꺾은 사람 계산
        for tid, positions in stats["track_history"].items():
            if tid not in stats["overlap_ids"] or len(positions) < 3:
                continue
            for i in range(1, len(positions) - 1):
                v1 = (positions[i][0] - positions[i-1][0], positions[i][1] - positions[i-1][1])
                v2 = (positions[i+1][0] - positions[i][0], positions[i+1][1] - positions[i][1])
                angle = calculate_angle(v1, v2)
                if angle > direction_change_angle:
                    stats["direction_change_ids"].add(tid)
                    break

        overlap_ratio = (len(stats["overlap_ids"]) / total_person_count * 100) if total_person_count else 0
        direction_change_ratio = (len(stats["direction_change_ids"]) / total_person_count * 100) if total_person_count else 0

        # 방해도 점수 계산
        score_val = (overlap_ratio * 0.4) + (avg_overlap_time * 5) + (direction_change_ratio * 0.4)
        if score_val < 30:
            level = "낮음"
        elif score_val < 60:
            level = "보통"
        else:
            level = "높음"

        analysis_text = (
            f"총 영상 시간: {total_video_time:.1f}초\n"
            f"총 등장 인원: {total_person_count}명\n"
            f"시설물과 겹친 사람 비율: {overlap_ratio:.1f}% ({len(stats['overlap_ids'])}명)\n"
            f"평균 겹침 시간: {avg_overlap_time:.2f}초\n"
            f"방향 꺾은 사람 비율: {direction_change_ratio:.1f}% ({len(stats['direction_change_ids'])}명)\n"
            f"종합 방해도: {level} (점수: {score_val:.1f})"
        )

        results.append({
            "box": damaged_facility_boxes[f_idx],
            "score": level,
            "analysis_text": analysis_text
        })

    return results