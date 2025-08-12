// src/api/inspection.client.ts
import { api } from "./http"; // ← 팀원이 만든 axios 인스턴스 경로로 맞추기

export type Frequency = "DAILY" | "WEEKLY" | "MONTHLY";

// 백엔드 InspectionSettingDTO와 키가 다르면 여기서만 이름 바꾸면 됨
export interface InspectionSettingDTO {
  startDate: string;        // "YYYY-MM-DD"
  startTime: string;        // "HH:mm" 또는 "HH:mm:ss"
  inspectionCycle: number;  // 주기(정수)
  //address: string;
}

export const saveInspectionSetting = (payload: InspectionSettingDTO) =>
  api.put("/api/inspection/setting", payload).then(res => res.data);
