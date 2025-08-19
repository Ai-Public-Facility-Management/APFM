import { api } from "./http";

export interface Facility {
  publicFaId: number;
  issueId: number;
  cameraName: string;
  publicFaType: string;
  condition: string;
  status: string;
  processing: boolean;
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

export interface FacilityDetail {
  id: number;
  cameraName: string;
  type: string;
  image: string;
  section: {
    width: number;
    height: number;
    ycenter: number;
    xcenter: number;
  };
  installDate: string;
  lastRepair: string;
  status: string;
  obstruction: number;
  estimate: number;
  estimateBasis: string;
}

export async function fetchFacilityDetail(id: number): Promise<FacilityDetail> {
  const response = await api.get("/api/publicfa/detail", { params: { id } });
  return response.data.publicFa;
}


/**
 * 제안 요청서 생성 API
 * @param ids 선택된 시설물 ID 배열
 */
export const createProposal = async (ids: number[]) => {
  try {
    const response = await api.post("/api/proposal/generate", ids, {
      headers: { "Content-Type": "application/json" }
    });
  } catch (error) {
    console.error("제안 요청서 전송 실패:", error);
    throw error;
  }
};

export interface ProposalData {
  project_name: string;
  project_overview: string;
  construction_period: string;
  site_analysis_summary: string[];
  estimation_details_with_basis: string[];
  total_cost: string;
  manpower_plan: string;
  equipment_plan: string;
  safety_quality_plan: string;
  conclusion_expected_effect: string;
}

export const fetchProposal = async (): Promise<ProposalData> => {
  const res = await api.get<{ proposal: ProposalData }>("http://localhost:8080/proposal/latest");
  return res.data.proposal;
};

export const saveProposal = async (proposal: ProposalData) => {
  try {
    const res = await api.post(
        "http://localhost:8080/proposal-to-docx",
        { proposal },
        {
          responseType: "blob", // DOCX는 바이너리 파일
        }
    );

    // 다운로드 처리
    const url = window.URL.createObjectURL(new Blob([res.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", "proposal.docx");
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error("DOCX 다운로드 실패:", error);
    throw error;
  }
};
