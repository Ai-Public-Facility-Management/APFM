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

// [기능 요약] 백엔드 InspectionDetailDTO와 1:1 매핑
export type IssueItem = {
  id: number;
  publicFaType?: string;
  type?: string;
  estimate?: number;
  estimateBasis?: string;
  obstruction?: string;
};

export type Camera = {
  cameraName: string;       // 카메라 이름(위치)
  imageUrl: string;         // 카메라 캡처 이미지
  issues: IssueItem[];      // 카메라별 이슈 목록
};

export type InspectionDetail = {
  id: number;               // 점검 ID
  createDate: string;       // 점검 날짜
  cameras: Camera[];        // 점검에 포함된 카메라 목록
  status: string;           // 보고서 작성 여부
};

export interface InspectionSettingDTO {
  startDate: string;        // "YYYY-MM-DD"
  startTime: string;        // "HH:mm" 또는 "HH:mm:ss"
  inspectionCycle: number;  // 주기(정수)
  //address: string;
}

// [기능 요약] 점검 리스트 조회 (백엔드: /api/inspection/all, 응답 {data: Page})
export const fetchInspectionList = async (
  page = 0,
  size = 10
): Promise<PageResponse<InspectionSummary>> => {
  const { data } = await api.get("/inspection/all", { params: { page, size } });
  return data.data as PageResponse<InspectionSummary>; // 백엔드가 { data: Page<...> }로 주므로
};

// [기능 요약] 점검 상세 조회 (백엔드에 신규 추가 예정)
export const fetchInspectionDetail = async (
  inspectionId: number
): Promise<InspectionDetail> => {
  const { data } = await api.get(`/inspection/${inspectionId}`);
  return data.data as InspectionDetail;
};

// [기능 요약] 점검 주기 설정 저장
export const saveInspectionSetting = (payload: InspectionSettingDTO) => {
  api.put("/inspection/setting", payload).then(res => res.data);
};

// [기능 요약] 보고서 생성 (LLM)
export const generateInspectionReport = async (issueIds: number[]) => {
  const { data } = await api.post("/inspection/generate", { issueIds });
  return data; // DTO 구조에 맞게 사용
};
