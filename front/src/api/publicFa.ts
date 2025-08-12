import { api } from "./http";

export interface Facility {
  publicFaId: number;
  issueId: number;
  cameraName: string;
  publicFaType: string;
  condition: string;
  status: string;
  isProcessing: boolean;
}

export interface FacilityPage {
  content: Facility[];
  totalElements: number;
  totalPages: number;
  // 필요한 페이징 정보 추가 가능
}

/**
 * 공공시설물 페이지 조회
 * @param page 현재 페이지 (0부터 시작)
 * @param size 페이지당 아이템 수
 * @returns 페이지 데이터
 */
export async function fetchFacilities(page: number, size: number): Promise<FacilityPage> {
    const response = await api.get("/api/publicfa/all", { params: { page, size } });
    return {
        content: response.data.publicFas.content,
        totalElements: response.data.publicFas.totalElements,
        totalPages: response.data.publicFas.totalPages
    };
}

