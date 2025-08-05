// SignupPage.tsx
import React, { useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import TermsModal from "./TermsModal";
import "../Login/LoginPage.css";
import "./SignupPage.css";
import koreaLogo from "../../assets/korea1.png";
import koreaLogo2 from "../../assets/korea2.png";
import search from "../../assets/search.png";
import login from "../../assets/login.png";
import signup from "../../assets/signup.png";

export default function SignUpPage() {
  const [form, setForm] = useState({
    username: "",
    email: "",
    authCode: "",
    department: "",
    password: "",
    confirmPassword: ""
  });
  const [emailSent, setEmailSent] = useState(false);
  const [emailVerified, setEmailVerified] = useState(false);
  const [showTermsModal, setShowTermsModal] = useState(true);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const sendVerificationCode = async () => {
    try {
      await axios.post("http://localhost:8082/api/auth/send-code", { email: form.email });
      alert("인증번호가 발송되었습니다.");
      setEmailSent(true);
    } catch (error) {
      alert("이메일 인증번호 발송에 실패했습니다.");
    }
  };

  const verifyCode = async () => {
    try {
      const res = await axios.post(`http://localhost:8082/api/auth/verify-code?email=${form.email}&code=${form.authCode}`);
      if (res.data === true || res.data.verified === true) {
        alert("이메일 인증이 완료되었습니다.");
        setEmailVerified(true);
      } else {
        alert("인증번호가 올바르지 않습니다.");
      }
    } catch (error) {
      alert("이메일 인증 확인 중 오류가 발생했습니다.");
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (form.password !== form.confirmPassword) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }
    if (!emailVerified) {
      alert("이메일 인증을 완료해주세요.");
      return;
    }
    try {
      await axios.post("http://localhost:8082/api/auth/signup", {
        email: form.email,
        password: form.password,
        username: form.username,
        department: form.department
      });
      alert("회원가입이 완료되었습니다. 관리자 승인 후 로그인할 수 있습니다.");
    } catch (error) {
      alert("회원가입에 실패했습니다.");
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

      {showTermsModal && <TermsModal onClose={() => setShowTermsModal(false)} />}

      {!showTermsModal && (
        <main className="mainContent">
          <div className="signupBox">
            <p className="signupGuide">공공시설물 관리자 회원가입</p>
            <h1 className="signupTitle">회원가입</h1>
            <hr />
            <form className="signupForm" onSubmit={handleSubmit}>
              <label>이름</label>
              <input name="username" value={form.username} onChange={handleChange} placeholder="이름을 입력하세요" required />

              <label>이메일</label>
              <input name="email" value={form.email} onChange={handleChange} placeholder="이메일을 입력하세요" required />
              <button type="button" className="email-button" onClick={sendVerificationCode}>인증번호 발송</button>


              <label>인증번호</label>
              <input name="authCode" value={form.authCode} onChange={handleChange} placeholder="인증번호를 입력하세요" required />
              <button type="button" className="verify-button" onClick={verifyCode}>확인</button>

              <label>소속 부서</label>
              <select name="department" value={form.department} onChange={handleChange} required>
                <option value="">선택해주세요</option>
                <option value="FACILITY">시설관리과</option>
                <option value="SAFETY">안전관리과</option>
                <option value="URBAN">도시계획과</option>
                <option value="CONSTRUCTION">건설과</option>
                <option value="ENVIRONMENT">환경과</option>
                <option value="FINANCE">재무과</option>
                <option value="CIVIL">민원과</option>
              </select>

              <label>비밀번호</label>
              <input type="password" name="password" value={form.password} onChange={handleChange} placeholder="비밀번호를 입력하세요" required />

              <label>비밀번호 확인</label>
              <input type="password" name="confirmPassword" value={form.confirmPassword} onChange={handleChange} placeholder="비밀번호를 다시 입력하세요" required />

              <div className={`password-warning ${form.password && form.confirmPassword 
                && form.password === form.confirmPassword ? 'match' : 'mismatch'}`}>
                {form.password && form.confirmPassword && form.password === form.confirmPassword ? (
                  <>
                    <span role="img" aria-label="확인">💡</span>
                    <div>
                      <strong>비밀번호 확인</strong>
                      <div className="password-subtext">비밀번호가 일치합니다.</div>
                    </div>
                  </>
                ) : (
                  <>
                    <span role="img" aria-label="경고">⚠️</span>
                    <div>
                      <strong>비밀번호 확인</strong>
                      <div className="password-subtext">동일한 비밀번호를 입력하세요</div>
                    </div>
                  </>
                )}
              </div>
              <button type="submit" className="submit-button">회원가입</button>
              <hr />
            </form>
          </div>
        </main>
      )}

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
