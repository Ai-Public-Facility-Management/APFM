// src/api/inspection.client.ts
import { api } from "./http"; // ← 팀원이 만든 axios 인스턴스 경로로 맞추기

export type Frequency = "DAILY" | "WEEKLY" | "MONTHLY";

// 백엔드 InspectionSettingDTO와 키가 다르면 여기서만 이름 바꾸면 됨
export interface InspectionSettingDTO {
  facilityId: number;
  frequency: Frequency;
  hour: number;
  minute: number;
  startAt: string;       // ISO: "2025-08-12T00:00:00Z"
  dayOfWeek?: number;    // WEEKLY: 1~7 (Mon=1)
  dayOfMonth?: number;   // MONTHLY: 1~28
  enabled: boolean;
}

export const saveInspectionSetting = (payload: InspectionSettingDTO) =>
  api.put("/api/inspection/setting", payload).then(res => res.data);
