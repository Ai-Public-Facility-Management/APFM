import { api } from "./http";

export interface BoardItem {
  id: number;
  title: string;
  type: string;
  authorName: string;
  authorDepartment: string;
  createdAt: string;
  imageUrl: string; // ✅ 게시글 대표 이미지 URL
}

export interface BoardPage {
  content: BoardItem[];
  totalElements: number;
  totalPages: number;
  // 필요에 따라 pageable 등 추가 가능
}

export interface BoardDetail {
  id: number;
  type: string;
  title: string;
  content: string;
  authorName: string;
  authorEmail: string;
  authorDepartment: string;
  createdAt: string;
  updatedAt: string;
  viewCount: number;
  commentCount: number;
  imageUrl?: string; // ✅ 본문 이미지 URL
  isAuthor: boolean; // ✅ 현재 로그인 사용자가 작성자인지 여부
}

export interface Comment {
  id: number;
  content: string;
  authorEmail: string;
  authorName: string;
  edited: boolean;
  createdAt: string;
  updatedAt: string;
  isAuthor: boolean;
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

// 게시글 상세 조회
export async function fetchBoardDetail(id: number): Promise<BoardDetail> {
  const response = await api.get(`/api/boards/${id}`);
  return response.data;
}

// 📌 댓글 목록 조회
export async function fetchComments(
  postId: number,
  page = 0,
  size = 10,
  sort = "latest"
): Promise<{ content: Comment[] }> {
  const response = await api.get(`/api/boards/${postId}/comments`, {
    params: { page, size, sort },
  });
  return response.data;
}

// 📌 댓글 작성
export async function createComment(postId: number, content: string): Promise<void> {
  await api.post(`/api/boards/${postId}/comments`, { content });
}

// 📌 댓글 수정
export async function updateComment(commentId: number, content: string): Promise<void> {
  await api.put(`/api/boards/comments/${commentId}`, { content });
}

// 📌 댓글 삭제
export async function deleteComment(commentId: number): Promise<void> {
  await api.delete(`/api/boards/comments/${commentId}`);
}