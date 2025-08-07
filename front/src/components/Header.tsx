// src/components/Header.tsx
import React from "react";
import { Link } from "react-router-dom";
import koreaLogo from "../assets/korea1.png";
import search from "../assets/search.png";
import login from "../assets/login.png";
import signup from "../assets/signup.png";
import "./Common.css";

const Header = () => (
  <header className="header">
    <div className="headerTop">
      <Link to="/" className="logo">
        <img src={koreaLogo} alt="정부로고" className="logo-image" />
        <span className="logo-text">시설닥터 | APFM</span>
      </Link>
      <div className="headerIcons">
        <Link to="/search">
          <img src={search} alt="검색" className="search-image" />
          <span>통합검색</span>
        </Link>
        <Link to="/login">
          <img src={login} alt="로그인" className="login-image" />
          <span>로그인</span>
        </Link>
        <Link to="/signup">
          <img src={signup} alt="회원가입" className="signup-image" />
          <span>회원가입</span>
        </Link>
      </div>
    </div>
  </header>
);

export default Header;
