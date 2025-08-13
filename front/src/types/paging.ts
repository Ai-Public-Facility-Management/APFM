// [기능 요약] 백엔드의 Page<T> 응답 구조 정의
export type PageResponse<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // 현재 페이지(0-base)
  size: number;
  first: boolean;
  last: boolean;
};
