import { api } from "./http";

export interface BoardItem {
  id: number;
  title: string;
  writerName: string;
  department: string;
  createdAt: string;
}

export interface BoardPage {
  content: BoardItem[];
  totalElements: number;
  totalPages: number;
  // 필요에 따라 pageable 등 추가 가능
}

/**
 * 게시글 목록 조회 (페이징, 검색)
 * @param page 0부터 시작하는 페이지 번호
 * @param size 페이지 크기
 * @param q 검색어 (옵션)
 * @returns 페이징된 게시글 데이터
 */
export async function fetchBoards(
  page: number,
  size: number,
  q?: string
): Promise<BoardPage> {
  const params: Record<string, any> = { page, size };
  if (q) params.q = q;

  const response = await api.get("/api/boards", { params });
  // 백엔드 API 응답 구조에 맞게 조정 필요
  return {
    content: response.data.content,
    totalElements: response.data.totalElements,
    totalPages: response.data.totalPages,
  };
}

/**
 * 특정 게시글 상세 조회
 * @param id 게시글 ID
 * @returns 게시글 상세 데이터 (필요하면 타입 정의 후 추가)
 */
// export async function fetchBoardDetail(id: number) {
//   const response = await api.get("/api/boards/" + id);
//   return response.data;
// }
