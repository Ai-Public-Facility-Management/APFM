// axios 설치 후 import
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import "./LoginPage.css";
import { LoginFormState } from "../../api/login";
import koreaLogo from "../../assets/korea1.png";
import koreaLogo2 from "../../assets/korea2.png";
import search from "../../assets/search.png";
import login from "../../assets/login.png";
import signup from "../../assets/signup.png";

export default function LoginPage() {
  const navigate = useNavigate(); // 로그인 후 페이지 이동을 위한 hook

  const [form, setForm] = useState<LoginFormState>({
    email: "",
    password: "",
    rememberId: false,
  });

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    try {
      const response = await axios.post(
        "/api/auth/login",
        {
          email: form.email, // 백엔드는 email 필드 사용
          password: form.password,
        },
        {
          withCredentials: true, // 세션 쿠키 포함
        }
      );

      alert(response.data);

      // 로그인 성공 시 페이지 이동
      navigate("/"); // 원하는 페이지로 이동

    } catch (error: any) {
      // 비밀번호 초기화
      setForm((prev) => ({ ...prev, password: "" }));
      
      if (error.response) {
        alert("로그인 실패: " + error.response.data.message);
      } else {
        alert("서버 오류: 로그인 요청 중 문제가 발생했습니다.");
      }
    }
  };

  return (
    <div className="container">
      <div className="topBanner">
        <img src={koreaLogo2} alt="태극기" className="flag-icon" />
        <span>이 누리집은 대한민국 공식 전자정부 누리집입니다.</span>
      </div>

      <header className="header">
        <div className="headerTop">
          <Link to="/" className="logo">
            <img src={koreaLogo} alt="대한민국정부" className="logo-image" />
            <span className="logo-text">시설닥터 | APFM</span>
          </Link>
          <div className="headerIcons">
            <Link to="/search">
              <img src={search} alt="검색" className="search-image" />
              <span>통합검색</span>
            </Link>
            <Link to="/login">
              <img src={login} alt="검색" className="login-image" />
              <span>로그인</span>
            </Link>
            <Link to="/signup">
              <img src={signup} alt="검색" className="signup-image" />
              <span>회원가입</span>
            </Link>
          </div>
        </div>
      </header>

      <main className="mainContent">
        <div className="loginBox">
          <p className="loginSubTitle">공공시설물 관리자 로그인</p>
          <h1 className="loginTitle">아이디/비밀번호 로그인</h1>
          <hr className="loginHr" />
          <form onSubmit={handleSubmit} className="loginForm">
            <div className="formGroup">
              <label htmlFor="email">아이디</label>
              <input
                type="text"
                id="email"
                name="email"
                placeholder="아이디를 입력하세요"
                value={form.email}
                onChange={handleChange}
                required
              />
            </div>
            <div className="formGroup">
              <label htmlFor="password">비밀번호</label>
              <input
                type="password"
                id="password"
                name="password"
                placeholder="비밀번호를 입력하세요"
                value={form.password}
                onChange={handleChange}
                required
              />
            </div>
            <div className="formCheck">
              <input
                type="checkbox"
                id="rememberId"
                name="rememberId"
                checked={form.rememberId}
                onChange={handleChange}
              />
              <label htmlFor="rememberId">아이디 저장</label>
            </div>
            <button type="submit" className="loginButton">
              로그인
            </button>
          </form>
          <div className="loginLinks">
            <Link to="/find-id">아이디 찾기</Link>
            <span>|</span>
            <Link to="/find-password">비밀번호 찾기</Link>
            <span>|</span>
            <Link to="/signup">회원가입</Link>
          </div>
          <hr className="loginHr" />
        </div>
      </main>

      <footer className="footer">
        <div className="footer-main">
          <div className="footer-left">
            <div className="footer-logo">
              <img src={koreaLogo} alt="로고" className="logo-image" />
              <span className="footer-text">시설닥터 | APFM</span>
            </div>
            <p>(04383) 서울특별시 용산구 이태원로 22</p>
            <p>
              <strong>대표전화</strong> 1234-5678 (유료, 평일 09시-18시)
            </p>
            <p>
              <strong>해외이용</strong> +82-1234-5678 (유료, 평일 09시-18시)
            </p>
          </div>

        </div>

        <hr />

        <div className="footer-bottom">
          <div className="footer-bottom-left">
            <div className="footer-links">
              <Link to="/terms" className="link-terms">이용약관</Link>
              <Link to="/privacy" className="link-privacy"><strong>개인정보처리방침</strong></Link>
              <span className="accessibility-mark">웹 접근성 품질인증 마크 획득</span>
            </div>
          </div>
          <div className="footer-bottom-right">
            © The Government of the Republic of Korea. All rights reserved.
          </div>
        </div>
      </footer>
    </div>
  );
}
