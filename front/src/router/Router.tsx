// src/router/Router.tsx
import React from "react";
import { createBrowserRouter, redirect } from "react-router-dom";
import LoginPage from "../features/Login/LoginPage";
import SignupPage from "../features/Signup/SignupPage";
import AdminPage from "../features/Admin/AdminPage";
// ✅ 당신의 토큰 읽는 함수로 교체하세요.
//    api/login에 getToken이 있다면 그걸 import해서 쓰면 베스트.
import { getToken } from "../api/login";

// 토큰이 있어야 접근 가능
const requireAuth = () => {
  const token = getToken?.() ?? null; // getToken이 없으면 직접 localStorage에서 읽어도 됨
  if (!token) {
    throw redirect("/login");
  }
  return null;
};

// 토큰이 있으면 접근 금지(로그인/회원가입 페이지 보호)
const onlyGuest = () => {
  const token = getToken?.() ?? null;
  if (token) {
    throw redirect("/");
  }
  return null;
};

export const router = createBrowserRouter([
  // 메인 페이지: 토큰 없으면 /login으로 튕김
  {
    path: "/",
    element: <AdminPage />,   // 👉 실제 메인 컴포넌트로 바꿔도 됨
    loader: requireAuth,
  },
  // 관리자 페이지도 보호
  {
    path: "/admin",
    element: <AdminPage />,
    loader: requireAuth,
  },
  // 로그인/회원가입: 토큰 있으면 / 로 리다이렉트
  {
    path: "/login",
    element: <LoginPage />,
    loader: onlyGuest,
  },
  {
    path: "/signup",
    element: <SignupPage />,
    loader: onlyGuest,
  },
  // 그 외 -> 메인으로
  {
    path: "*",
    loader: () => redirect("/"),
  },
]);
