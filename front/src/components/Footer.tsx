// src/components/Footer.tsx
import React from "react";
import { Link } from "react-router-dom";
import koreaLogo from "../assets/korea1.png";
import "./Common.css";

const Footer = () => (
  <footer className="footer">
    <div className="footer-main">
      <div className="footer-left">
        <div className="footer-logo">
          <img src={koreaLogo} alt="로고" className="logo-image" />
          <span className="footer-text">시설닥터 | APFM</span>
        </div>
        <p>(04383) 서울특별시 용산구 이태원로 22</p>
        <p><strong>대표전화</strong> 1234-5678 (유료, 평일 09시-18시)</p>
        <p><strong>해외이용</strong> +82-1234-5678 (유료, 평일 09시-18시)</p>
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
);

export default Footer;
