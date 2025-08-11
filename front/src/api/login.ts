// src/api/login.ts
import { api } from "./http";

export interface LoginResponse {
  token: string;
  userType: string; // "ADMIN" | "USER" ...
  message: string;
}

export async function loginAPI(data: { email: string; password: string; rememberId: boolean; }): Promise<LoginResponse> {
  const res = await api.post<LoginResponse>("/api/auth/login", data);
  // 토큰 필수 확인
  if (!res.data?.token) throw new Error("로그인 응답에서 토큰을 찾지 못했습니다.");
  return res.data;
}

// JWT payload에서 role 추출 (토큰 없거나 파싱 실패 시 null)
export const getRoleFromToken = (): string | null => {
  const t = getToken();
  if (!t) return null;
  try {
    const payload = JSON.parse(atob(t.split(".")[1]));
    return payload?.role ?? null; // "ADMIN" | "USER" | null
  } catch {
    return null;
  }
};


// 토큰 저장/조회 유틸
export const TOKEN_KEY = "token";
export const saveToken = (t: string) => localStorage.setItem(TOKEN_KEY, t);
export const getToken = () => localStorage.getItem(TOKEN_KEY);
export const clearToken = () => localStorage.removeItem(TOKEN_KEY);

// 아이디 저장/조회 유틸
export const ID_KEY = "rememberedEmail";
export const saveEmail = (email: string) => localStorage.setItem(ID_KEY, btoa(email));
export const getSavedEmail = () => localStorage.getItem(ID_KEY);
export const clearEmail = () => localStorage.removeItem(ID_KEY);


// 로그아웃도 api 사용 (Authorization 자동 주입)
export async function logoutAPI(): Promise<string> {
  const res = await api.post("/api/auth/logout", null, { responseType: "text" });
  return res.data;
}