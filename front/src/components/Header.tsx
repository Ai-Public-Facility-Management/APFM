// src/components/Header.tsx
import React, { useEffect, useState, useCallback } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import koreaLogo from "../assets/korea1.png";
import search from "../assets/ico_sch.svg";
import loginImg from "../assets/ico_login.svg";
import signup from "../assets/ico_join.svg";

// 로그인 후에 쓸 PNG 아이콘 (네가 만든 파일 경로/이름에 맞춰 수정)
import settings from "../assets/ico_calendar.svg";
import userIcon from "../assets/ico_my.svg";
import logoutIcon from "../assets/ico_logout.svg";

import {
  getToken,
  clearToken,
  getSavedEmail,
  logoutAPI,
} from "../api/login";
import "./Common.css";

const Header: React.FC = () => {
  const nav = useNavigate();
  const location = useLocation();

  const [authed, setAuthed] = useState<boolean>(!!getToken());
  const [userName, setUserName] = useState<string>("사용자");
  const [loggingOut, setLoggingOut] = useState<boolean>(false);

  // 라우트 이동 시 토큰 상태 재확인
  useEffect(() => {
    setAuthed(!!getToken());
  }, [location.pathname]);

  // 저장된 이메일에서 사용자명 추출
  useEffect(() => {
    const saved = getSavedEmail();
    if (!saved) return;
    try {
      const email = atob(saved);
      setUserName(email.split("@")[0] || "사용자");
    } catch {
      setUserName("사용자");
    }
  }, []);

  // 로그아웃
  const onLogout = useCallback(async () => {
    if (loggingOut) return;
    setLoggingOut(true);
    try {
      const token = getToken();
      if (token) {
        // 백엔드에 실제 로그아웃(블랙리스트) 요청
        await logoutAPI(token);
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
          <span className="logo-text">시설닥터 | APFM</span>
        </Link>

        {/* 오른쪽 아이콘들: 기존 디자인 유지 (세로 배치) */}
        {!authed ? (
          <div className="headerIcons">
            <Link to="/login">
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
            {/* 통합검색 유지 */}
            <Link to="/search">
              {/* 새 아이콘도 기존 크기 규칙을 재사용하려면 className을 search-image로 통일해도 됩니다 */}
              <img src={search} alt="검색" className="search-image" />
              <span>통합검색</span>
            </Link>

            {/* 점검 주기 설정 */}
            <Link to="/inspection/interval">
              <img src={settings} alt="설정" className="search-image" />
              <span>점검 주기 설정</span>
            </Link>

            {/* 사용자명 */}
            <div className="userItem">
              <img src={userIcon} alt="사용자" className="search-image" />
              <span>{userName} 님</span>
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
