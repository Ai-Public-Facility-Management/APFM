// src/router/Router.tsx
import React from "react";
import { createBrowserRouter, redirect } from "react-router-dom";
import LoginPage from "../features/Login/LoginPage";
import SignupPage from "../features/Signup/SignupPage";
import AdminPage from "../features/Admin/AdminPage";
import MainPage from "../features/Main/MainPage";
import FindPasswordPage from "../features/Findpassword/Findpasswordpage";

import InspectionListPage from "../features/Inspections/InspectionListPage";
import InspectionDetailPage from "../features/Inspections/InspectionDetailPage";
import DetailPublicFa from "../features/PublicFa/DetailPublicFa";
import FacilityList from "../features/Facility/FacilityList";
import BoardDetail from "../features/Board/BoardDetail";
import {getRoleFromToken, getToken} from "../api/login";
import BoardWrite from "../features/Board/BoardWrite";
import BoardList from "../features/Board/BoardList";

// 토큰이 있어야 접근 가능
const requireAuth = () => {
  const token = getToken?.() ?? null;
  if (!token) throw redirect("/login");
  return null;
};

// 관리자만 접근 가능
const requireAdmin = () => {
  const token = getToken?.() ?? null;
  if (!token) throw redirect("/login");
  const role = getRoleFromToken?.();
  if (role !== "ADMIN") throw redirect("/"); // 일반 유저는 메인으로
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
    element: <MainPage />,   // 👉 실제 메인 컴포넌트로 바꿔도 됨
    loader: requireAuth,
  },
  // 관리자 페이지도 보호
  {
    path: "/admin",
    element: <AdminPage />,
    loader: requireAdmin,
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
  {
    path: "/inspections",
    element: <InspectionListPage />,
    loader: requireAuth,
  },
  {
    path: "/inspections/:id",
    element: <InspectionDetailPage />,
    loader: requireAuth,
  },

  {
    path: "/find-password",
    element: <FindPasswordPage />,
    loader: onlyGuest,
  },

  {
    path: "/detail/:id",
    element: <DetailPublicFa/>,
    loader: requireAuth,
  },

  {
    path: "/facility-list",
    element: <FacilityList />,
    loader: requireAuth,
  },
  {
    path: "/board",
    element: <BoardList />,
    loader: requireAuth,
  },
  {
      path: "/board/:postId",  // ✅ 상세 페이지 추가
      element: <BoardDetail />,
      loader: requireAuth,
  },

  {
    path: "/board/write", // ✨ 작성 페이지 라우트
    element: <BoardWrite />,
    loader: requireAuth,
  },


  // 그 외 -> 메인으로
  {
    path: "*",
    loader: () => redirect("/"),
  }

]);
