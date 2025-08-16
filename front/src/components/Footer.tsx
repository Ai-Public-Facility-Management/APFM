import React, { useState } from "react";
import { Link } from "react-router-dom";
import koreaLogo from "../assets/korea1.png";
import TermsModalForFooter from "./TermsModalForFooter";
import "./Common.css";

const Footer = () => {
  const [showTerms, setShowTerms] = useState(false);
  const [showPrivacy, setShowPrivacy] = useState(false);

  return (
    <>
      <footer className="footer">
        <div className="footer-main">
          <div className="footer-left">
            <div className="footer-logo">
              <img src={koreaLogo} alt="로고" className="logo-image" />
              <span className="footer-text">공공시설물 관리 | APFM</span>
            </div>
            <p>(48819) 부산광역시 동구 초량중로 29</p>
            <p><strong>대표전화</strong> 1234-5678 (유료, 평일 09시-18시)</p>
            <p><strong>해외이용</strong> +82-1234-5678 (유료, 평일 09시-18시)</p>
          </div>
        </div>

        <hr />

        <div className="footer-bottom">
          <div className="footer-bottom-left">
            <div className="footer-links">
              <button className="link-terms" onClick={() => setShowTerms(true)}>이용약관</button>
              <button className="link-privacy" onClick={() => setShowPrivacy(true)}><strong>개인정보처리방침</strong></button>
              <span className="accessibility-mark">웹 접근성 품질인증 마크 획득</span>
            </div>
          </div>
          <div className="footer-bottom-right">
            © The Government of the Republic of Korea. All rights reserved.
          </div>
        </div>
      </footer>

      {/* 모달 렌더링 */}
      {showTerms && (
        <TermsModalForFooter
          clauseName="termsOfUse"
          onClose={() => setShowTerms(false)}
        />
      )}

      {showPrivacy && (
        <TermsModalForFooter
          clauseName="privacyNotice"
          onClose={() => setShowPrivacy(false)}
        />
      )}
    </>
  );
};

export default Footer;
