import axios from "axios";
import { getToken } from "./login";

export interface Facility {
  id: number;
  type: string;
  section: string;
  installDate: string;
  status: string;
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
    const token = getToken();
    const response = await axios.get("/api/publicfa/all", {
        params: { page, size },
        headers: token ? { Authorization: `Bearer ${token}` } : undefined,
    });
    return response.data.publicFas;
}
