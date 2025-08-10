// src/api/signup.ts
import axios from "axios";

export interface SignUpRequest {
  email: string;
  password: string;
  username: string;
  department: string;
}

/** 인증코드 발송: 문자열 메시지 반환 */
export const sendVerificationCode = async (email: string) => {
  const res = await axios.post<string>(
    "/api/auth/send-code",
    null,
    { params: { email }, responseType: "text" }
  );
  return res.data; // "인증 코드 전송 완료"
};

/** 인증코드 검증: 문자열 메시지 반환 */
export const verifyCode = async (email: string, code: string) => {
  const res = await axios.post<string>(
    "/api/auth/verify-code",
    null,
    { params: { email, code }, responseType: "text" }
  );
  return res.data; // "인증 성공" or "인증 실패"
};

/** 회원가입 제출: 문자열 메시지 반환 */
export const submitSignUp = async (data: SignUpRequest) => {
  const res = await axios.post<string>(
    "/api/auth/signup",
    data,
    { responseType: "text" }
  );
  return res.data; // "회원가입 성공" or 에러 메시지
};