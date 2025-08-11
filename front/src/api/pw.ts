// src/api/pw.ts
import { api } from "./http"; // 네가 올린 axios 인스턴스 파일 경로에 맞게

export interface ResetCodeRequest {
  email: string;
}
export interface VerifyCodeRequest {
  email: string;
  code: string; // "123456"
}
export interface ResetPasswordRequest {
  email: string;
  code: string;
  password: string;
}

// (1) 비번 재설정 코드 요청 (항상 200 OK 반환)
export const requestResetCodeAPI = (payload: ResetCodeRequest) =>
  api.post<void>("/users/reset-code", payload);

// (2) 코드 검증 (true/false)
export const verifyResetCodeAPI = (payload: VerifyCodeRequest) =>
  api.post<boolean>("/users/reset-code/verify", payload);

// (3) 최종 비밀번호 변경
export const resetPasswordAPI = (payload: ResetPasswordRequest) =>
  api.post<string>("/users/reset-password", payload);
