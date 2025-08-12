// [기능 요약] 점검(Inspection) 관련 API: 리스트/상세/설정/보고서 생성
import { api } from "./http";
import type { PageResponse } from "../types/paging";

export type Frequency = "DAILY" | "WEEKLY" | "MONTHLY";

export type InspectionSummary = {
  id: number;
  createDate: string;
  status: string;
  repairCount: number;
  removalCount: number;
  hasIssue: boolean;
  hasReport: boolean;
};

export type InspectionDetail = {
  id: number;
  createDate?: string;
  status?: string;
  facilityName?: string;
  location?: string;
  description?: string;
  content?: string;
  imageUrlList?: string[];
  issues?: Array<{
    id: number;
    facilityCategory?: string;
    type?: string;
    status?: string;
    severity?: number;
    level?: number;
    count?: number;
    estimate?: number;
    estimateBasis?: string;
    description?: string;
    content?: string;
    imageUrl?: string;
    aiImagePath?: string;
  }>;
};

export interface InspectionSettingDTO {
  startDate: string;        // "YYYY-MM-DD"
  startTime: string;        // "HH:mm" 또는 "HH:mm:ss"
  inspectionCycle: number;  // 주기(정수)
  address: string;
}

// [기능 요약] 점검 리스트 조회 (백엔드: /api/inspection/all, 응답 {data: Page})
export const fetchInspectionList = async (
  page = 0,
  size = 10
): Promise<PageResponse<InspectionSummary>> => {
  const { data } = await api.get("/api/inspection/all", { params: { page, size } });
  return data.data as PageResponse<InspectionSummary>; // 백엔드가 { data: Page<...> }로 주므로
};

// [기능 요약] 점검 상세 조회 (백엔드에 신규 추가 예정)
export const fetchInspectionDetail = async (inspectionId: number) => {
  const { data } = await api.get(`/api/inspection/${inspectionId}`);
  return data.data as InspectionDetail;
};

// [기능 요약] 점검 주기 설정 저장
export const saveInspectionSetting = (payload: InspectionSettingDTO) => {
  api.put("/api/inspection/setting", payload).then(res => res.data);
};

// [기능 요약] 보고서 생성 (LLM)
export const generateInspectionReport = async (issueIds: number[]) => {
  const { data } = await api.post("/api/inspection/generate", { issueIds });
  return data; // DTO 구조에 맞게 사용
};
