// src/components/Header.tsx
import React, { useEffect, useState, useCallback } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import koreaLogo from "../assets/korea1.png";
import board from "../assets/ico_faq.svg"
import loginImg from "../assets/ico_login.svg";
import signup from "../assets/ico_join.svg";

// 로그인 후에 쓸 PNG 아이콘
import settings from "../assets/ico_calendar.svg";
import userIcon from "../assets/ico_my.svg";
import logoutIcon from "../assets/ico_logout.svg";
import { useScheduleModal } from "../features/schedule/ScheduleModalProvider";

import {
  getToken,
  clearToken,
  getSavedEmail,
  logoutAPI,
  getUserName
} from "../api/login";
import "./Common.css";

const Header: React.FC = () => {
  const nav = useNavigate();
  const location = useLocation();

  const [authed, setAuthed] = useState<boolean>(!!getToken());
  const [userName, setUserName] = useState<string>("사용자");
  const [loggingOut, setLoggingOut] = useState<boolean>(false);

  const { open } = useScheduleModal();
  

  // 라우트 이동 시 토큰 상태 재확인
  useEffect(() => {
    setAuthed(!!getToken());
  }, [location.pathname]);

  // 저장된 이메일에서 사용자명 추출
  useEffect(() => {
    const name = getUserName();
    if (name) setUserName(name);
  }, []);

  // 이름 마스킹 함수
  const maskName = (name: string) => {
    if (!name) return "";
      {
        if (name.length <= 2) return name[0] + "*";
        return name[0] + "*".repeat(name.length - 2) + name[name.length - 1];
      }
  };

  // 로그아웃
  const onLogout = useCallback(async () => {
    if (loggingOut) return;
    setLoggingOut(true);
    try {
      const token = getToken();
      if (token) {
        // 백엔드에 실제 로그아웃(블랙리스트) 요청
        await logoutAPI();
      }
    } catch {
      // 서버 에러여도 UX 위해 로컬 토큰은 지움
    } finally {
      clearToken();
      setAuthed(false);
      setLoggingOut(false);
      nav("/login", { replace: true });
    }
  }, [nav, loggingOut]);

  return (
    <header className="header">
      <div className="headerTop">
        <Link to="/" className="logo">
          <img src={koreaLogo} alt="정부로고" className="logo-image" />
          <span className="logo-text">공공시설물 관리 | APFM</span>
        </Link>

        {/* 오른쪽 아이콘들: 기존 디자인 유지 (세로 배치) */}
        {!authed ? (
          <div className="headerIcons">
            <Link
              to="/login"
              state={{ scrollToTop: true }}            // ✅ 로그인 페이지에 신호 전달
              onClick={() => window.scrollTo({ top: 0, left: 0, behavior: "auto" })} // 보수적으로 한번
            >
              <img src={loginImg} alt="로그인" className="login-image" />
              <span>로그인</span>
            </Link>
            <Link to="/signup">
              <img src={signup} alt="회원가입" className="signup-image" />
              <span>회원가입</span>
            </Link>
          </div>
        ) : (
          <div className="headerIcons">
            <Link to="/board">
              <img src={board} alt="검색" className="search-image" />
              <span>게시판</span>
            </Link>
          <button
              type="button"
              onClick={() => open()}
              style={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                background: "none",
                border: 0,
                padding: 0,
                color: "#000",
                font: "inherit",
                fontWeight: "bold",
                cursor: "pointer",
                textDecoration: "none",
              }}
          >
              <img src={settings} alt="설정" className="search-image" />
              <span>점검 주기 설정</span>
          </button>

            {/* 사용자명 */}
            <div className="userItem">
              <img src={userIcon} alt="사용자" className="search-image" />
              <span>{maskName(userName)} 님</span>
            </div>

            {/* 로그아웃 버튼 (세로 배치 맞춤) */}
            <button
              type="button"
              onClick={onLogout}
              disabled={loggingOut}
              aria-label="로그아웃"
              style={{
                display: "flex",
                flexDirection: "column",
                alignItems: "center",
                background: "none",
                border: 0,
                padding: 0,
                color: "#000",
                font: "inherit",
                fontWeight: "bold",
                cursor: loggingOut ? "not-allowed" : "pointer",
                textDecoration: "none",
              }}
            >
              <img src={logoutIcon} alt="로그아웃" className="search-image" />
              <span>{loggingOut ? "로그아웃 중…" : "로그아웃"}</span>
            </button>
          </div>
        )}
      </div>
    </header>
  );
};

export default Header;
