// signup.ts (API 함수 정의)
import axios from "axios";

export interface SignUpRequest {
  email: string;
  password: string;
  username: string;
  department: string;
}

export const sendVerificationCode = async (email: string) => {
  return await axios.post(`/api/auth/send-code?email=${email}`);
};

export const verifyCode = async (email: string, code: string) => {
  return await axios.post(`/api/auth/verify-code?email=${email}&code=${code}`);
};

export const submitSignUp = async (data: SignUpRequest) => {
  return await axios.post(`/api/auth/signup`, data);
};