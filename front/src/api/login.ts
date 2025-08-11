// src/api/login.ts
import axios from "axios";

export interface LoginFormState {
  email: string;
  password: string;
  rememberId: boolean;
}

// /** 백엔드 문자열 응답에서 토큰만 추출 ("로그인 성공: <TOKEN>") */
// function extractToken(text: string): string {
//   const m = text.match(/로그인 성공:\s*(.+)$/);
//   return m ? m[1].trim() : "";
// }
//
// /** 로그인 → 토큰 문자열 반환 (서버 에러는 axios 에러로 그대로 throw) */
// export async function loginAPI(data: LoginFormState): Promise<string> {
//   const { email, password } = data;
//   const res = await axios.post("/api/auth/login", { email, password }, { responseType: "text" });
//   const token = extractToken(res.data);
//   if (!token) {
//     // 200인데 토큰 파싱 실패한 경우만 로컬 에러
//     throw new Error("로그인 응답에서 토큰을 찾지 못했습니다.");
//   }
//   return token;
// }
// JSON 응답 타입
export interface LoginResponse {
  token: string;
  userType: string; // "ADMIN" | "INSPECTOR" 등
  message: string;
}

/** ✅ 신규 방식: JSON 전체 반환 + 토큰 검증 */
export async function loginAPI(data: LoginFormState): Promise<LoginResponse> {
  const { email, password } = data;
  const res = await axios.post<LoginResponse>(
    "/api/auth/login",
    { email, password },
    { withCredentials: true }
  );

  // 토큰 유효성 체크
  if (!res.data?.token) {
    throw new Error("로그인 응답에서 토큰을 찾지 못했습니다.");
  }

  return res.data; // { token, userType, message }
}

/** 로그아웃 (Authorization 헤더 필요) */
export async function logoutAPI(token: string): Promise<string> {
  const res = await axios.post(
    "/api/auth/logout",
    null,
    {
      headers: { Authorization: `Bearer ${token}` },
      responseType: "text",
    }
  );
  return res.data; // "로그아웃 성공 (토큰 블랙리스트 등록)"
}

/** 로컬 스토리지 유틸 */
export const TOKEN_KEY = "token";
export const saveToken = (t: string) => localStorage.setItem(TOKEN_KEY, t);
export const getToken = () => localStorage.getItem(TOKEN_KEY);
export const clearToken = () => localStorage.removeItem(TOKEN_KEY);

/** 아이디 저장 유틸 (기존 base64 방식 호환) */
export const ID_KEY = "rememberedEmail";
export const saveEmail = (email: string) => localStorage.setItem(ID_KEY, btoa(email));
export const getSavedEmail = () => localStorage.getItem(ID_KEY);
export const clearEmail = () => localStorage.removeItem(ID_KEY);
