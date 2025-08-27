// src/api/ai.ts
import axios from "axios";

const API_URL = "/ai/predict-board";

/**
 * 단일 객체 감지 결과 타입
 */
export interface DetectionResult {
  class: string;
  confidence: number;
  box: [number, number, number, number];
  expand_box: [number, number, number, number];
  status: string | null;
  vision_analysis: string;
  estimate: number | null;
  estimate_basis: string | null;
}

/**
 * /predict-board API 전체 응답 타입
 */
export interface PredictBoardResponse {
  detections: DetectionResult[];
}

/**
 * AI 분석 요청 (이미지 업로드)
 * @param file 업로드할 이미지 파일 (input[type=file]에서 가져온 File 객체)
 * @returns 백엔드의 분석 결과 JSON
 */
export async function predictBoard(file: File): Promise<PredictBoardResponse> {
  try {
    const formData = new FormData();
    formData.append("image", file);

    const { data } = await axios.post<PredictBoardResponse>(API_URL, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    return data; // 백엔드에서 받은 분석 결과
  } catch (error: any) {
    console.error("🚨 predictBoard API 호출 실패", error);
    throw error;
  }
}
