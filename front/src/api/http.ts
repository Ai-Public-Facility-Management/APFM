import axios from "axios";
import { getToken } from "./login";

export const api = axios.create({
    baseURL: process.env.REACT_APP_API_BASE ?? "/api", // 환경에 맞게
    // withCredentials: false // Bearer 토큰이면 보통 불필요
});

// 요청마다 Authorization 자동 주입
api.interceptors.request.use((config) => {
    const token = getToken();
    if (token) {
        config.headers = config.headers ?? {};
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
});