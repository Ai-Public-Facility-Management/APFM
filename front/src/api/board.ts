// src/api/board.ts
import { api } from "./http";

// ===== 인터페이스 =====
export interface Attachment {
  id: number;
  originalName: string;
  storedUrl: string;
}

export interface PostListItem {
  id: number;
  title: string;
  authorEmail: string;
  department: string;
  createdAt: string;
  viewCount: number;
}

export interface PostDetail {
  id: number;
  type: string;
  title: string;
  content: string;
  pinned: boolean;
  viewCount: number;
  authorEmail: string;
  authorName: string;
  department: string;
  commentCount: number;
  createdAt: string;
  updatedAt: string;
  attachments: Attachment[];
}

export interface Comment {
  id: number;
  content: string;
  authorEmail: string;
  authorName: string;
  edited: boolean;
  createdAt: string;
  updatedAt: string;
}

// ===== API 함수 =====

// 📌 게시글 목록 조회 (검색 + 페이지네이션)
export const getPosts = (
  page = 0,
  size = 10,
  keyword = "",
  sort = "createdAt,desc"
) =>
  api.get<{ content: PostListItem[]; totalPages: number; totalElements: number }>(
    "/boards",
    {
      params: { page, size, keyword, sort },
    }
  );

// 📌 게시글 상세 조회
export const getPostDetail = (postId: string) =>
  api.get<PostDetail>(`/api/boards/${postId}`);

// 📌 댓글 목록 조회
export const getComments = (
  postId: string,
  page = 0,
  size = 10,
  sort = "latest"
) =>
  api.get<{ content: Comment[] }>(`/api/boards/${postId}/comments`, {
    params: { page, size, sort },
  });

// 📌 댓글 작성
export const addComment = (postId: string, content: string) =>
  api.post(`/api/boards/${postId}/comments`, { content });

// 📌 댓글 수정
export const updateComment = (commentId: number, content: string) =>
  api.put(`/api/boards/comments/${commentId}`, { content });

// 📌 댓글 삭제
export const deleteComment = (commentId: number) =>
  api.delete(`/api/boards/comments/${commentId}`);