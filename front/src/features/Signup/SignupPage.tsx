// src/features/Signup/SignupPage.tsx
import React, { useState } from "react";
import { Link , useNavigate } from "react-router-dom";
import axios from "axios";
import Layout from "../../components/Layout";
import TermsModal from "./TermsModal";
import "./SignupPage.css";

export default function SignUpPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: "",
    email: "",
    authCode: "",
    department: "",
    password: "",
    confirmPassword: "",
  });
  const [emailSent, setEmailSent] = useState(false);
  const [emailVerified, setEmailVerified] = useState(false);
  const [showTermsModal, setShowTermsModal] = useState(true);
  const [emailSendDisabled, setEmailSendDisabled] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm({ ...form, [name]: value });
  };

  const validateEmail = (email: string) => {
    const emailRegex = /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/;
    return emailRegex.test(email);
  };

  const sendVerificationCode = async () => {
    if (!validateEmail(form.email)) {
      alert("올바른 이메일 형식을 입력해주세요.");
      return;
    }

    try {
      await axios.post(`/api/auth/send-code?email=${form.email}`);
      alert("인증번호가 발송되었습니다.");
      setEmailSent(true);
    } catch (error) {
      alert("이메일 인증번호 발송에 실패했습니다.");
    }
  };

  const verifyCode = async () => {
    try {
      const res = await axios.post(
        `/api/auth/verify-code?email=${form.email}&code=${form.authCode}`
      );

      if (res.status === 200 && res.data === "인증 성공") {
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
      await axios.post("/api/auth/signup", {
        email: form.email,
        password: form.password,
        username: form.username,
        department: form.department,
      });
      alert("회원가입이 완료되었습니다. 관리자 승인 후 로그인할 수 있습니다.");
      setTimeout(() => navigate("/login"), 0);
    } catch (error: any) {
      console.error("회원가입 에러:", error); // 👈 로그 찍기
      setForm((prev) => ({ ...prev, password: "", confirmPassword: "" }));

      if (error.response?.data) {
        alert(`회원가입에 실패했습니다: ${error.response.data}`);
      } else {
        alert("서버 오류: 회원가입 요청 중 문제가 발생했습니다.");
      }
    }
  };

  return (
    <Layout>
      {showTermsModal ? (
        <TermsModal onClose={() => setShowTermsModal(false)} />
      ) : (
        <main className="signupContainer">
          <div className="signupBox">
            <p className="signupGuide">공공시설물 관리자 회원가입</p>
            <h1 className="signupTitle">회원가입</h1>
            <hr />
            <form className="signupForm" onSubmit={handleSubmit}>
              <label>이름</label>
              <input
                name="username"
                value={form.username}
                onChange={handleChange}
                placeholder="이름을 입력하세요"
                required
              />

              <label>이메일</label>
              <input
                name="email"
                value={form.email}
                onChange={handleChange}
                placeholder="이메일을 입력하세요"
                required
              />
              <button type="button" className="email-button" onClick={sendVerificationCode}>
                인증번호 발송
              </button>

              <label>인증번호</label>
              <input
                name="authCode"
                value={form.authCode}
                onChange={handleChange}
                placeholder="인증번호를 입력하세요"
                required
              />
              <button type="button" className="verify-button" onClick={verifyCode}>
                확인
              </button>

              <label>소속 부서</label>
              <select name="department" value={form.department} onChange={handleChange} required>
                <option value="">선택해주세요</option>
                <option value="DEVELOPMENT">개발부서</option>
                <option value="DESIGN">디지인부서</option>
                <option value="MARKETING">마케팅부서</option>
                <option value="SALES">영업부서</option>
                <option value="HR">인사과</option>
                <option value="FINANCE">재무과</option>
              </select>

              <label>비밀번호</label>
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={handleChange}
                placeholder="비밀번호를 입력하세요"
                required
              />

              <label>비밀번호 확인</label>
              <input
                type="password"
                name="confirmPassword"
                value={form.confirmPassword}
                onChange={handleChange}
                placeholder="비밀번호를 다시 입력하세요"
                required
              />

              <div
                className={`password-warning ${
                  form.password &&
                  form.confirmPassword &&
                  form.password === form.confirmPassword
                    ? "match"
                    : "mismatch"
                }`}
              >
                {form.password && form.confirmPassword && form.password === form.confirmPassword ? (
                  <>
                    <span role="img" aria-label="확인">
                      💡
                    </span>
                    <div>
                      <strong>비밀번호 확인</strong>
                      <div className="password-subtext">비밀번호가 일치합니다.</div>
                    </div>
                  </>
                ) : (
                  <>
                    <span role="img" aria-label="경고">
                      ⚠️
                    </span>
                    <div>
                      <strong>비밀번호 확인</strong>
                      <div className="password-subtext">동일한 비밀번호를 입력하세요</div>
                    </div>
                  </>
                )}
              </div>

              <button type="submit" className="submit-button">
                회원가입
              </button>
              <hr />
            </form>
          </div>
        </main>
      )}
    </Layout>
  );
}
