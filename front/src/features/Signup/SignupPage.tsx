// src/features/Signup/SignupPage.tsx
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import TermsModal from "./TermsModal";
import "./SignupPage.css";

import {
  sendVerificationCode as apiSendCode,
  verifyCode as apiVerifyCode,
  submitSignUp as apiSubmitSignUp,
} from "../../api/signup";

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

  const [loading, setLoading] = useState({
    send: false,
    verify: false,
    submit: false,
  });

  const onChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setForm((p) => ({ ...p, [name]: value }));
  };

  const validateEmail = (email: string) =>
    /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/.test(email);

  const handleSendCode = async () => {
    if (!validateEmail(form.email)) {
      alert("올바른 이메일 형식을 입력해주세요.");
      return;
    }
    try {
      setLoading((p) => ({ ...p, send: true }));
      const msg = await apiSendCode(form.email); // "인증 코드 전송 완료"
      alert(msg || "인증번호가 발송되었습니다.");
      setEmailSent(true);
    } catch (e: any) {
      const msg = e?.response?.data ?? "이메일 인증번호 발송에 실패했습니다.";
      alert(msg);
    } finally {
      setLoading((p) => ({ ...p, send: false }));
    }
  };

  const handleVerifyCode = async () => {
    if (!emailSent) {
      alert("먼저 인증번호를 발송하세요.");
      return;
    }
    try {
      setLoading((p) => ({ ...p, verify: true }));
      const msg = await apiVerifyCode(form.email, form.authCode); // "인증 성공"
      if (msg === "인증 성공") {
        alert("이메일 인증이 완료되었습니다.");
        setEmailVerified(true);
      } else {
        alert("인증번호가 올바르지 않습니다.");
      }
    } catch (e: any) {
      const msg = e?.response?.data ?? "이메일 인증 확인 중 오류가 발생했습니다.";
      alert(msg);
    } finally {
      setLoading((p) => ({ ...p, verify: false }));
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!emailVerified) {
      alert("이메일 인증을 완료해주세요.");
      return;
    }
    if (form.password !== form.confirmPassword) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }
    try {
      setLoading((p) => ({ ...p, submit: true }));
      const msg = await apiSubmitSignUp({
        email: form.email,
        password: form.password,
        username: form.username,
        department: form.department, // ENUM 이름과 일치해야 함
      }); // "회원가입 성공"
      alert(msg || "회원가입이 완료되었습니다. 관리자 승인 후 로그인할 수 있습니다.");
      navigate("/login", { replace: true });
    } catch (e: any) {
      setForm((p) => ({ ...p, password: "", confirmPassword: "" }));
      const msg = e?.response?.data ?? "서버 오류: 회원가입 요청 중 문제가 발생했습니다.";
      alert(`회원가입에 실패했습니다: ${msg}`);
    } finally {
      setLoading((p) => ({ ...p, submit: false }));
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
                onChange={onChange}
                placeholder="이름을 입력하세요"
                required
              />

              <label>이메일</label>
              <input
                name="email"
                value={form.email}
                onChange={onChange}
                placeholder="이메일을 입력하세요"
                required
              />
              <button
                type="button"
                className="email-button"
                onClick={handleSendCode}
                disabled={loading.send}
              >
                {loading.send ? "발송 중..." : "인증번호 발송"}
              </button>

              <label>인증번호</label>
              <input
                name="authCode"
                value={form.authCode}
                onChange={onChange}
                placeholder="인증번호를 입력하세요"
                required
              />
              <button
                type="button"
                className="verify-button"
                onClick={handleVerifyCode}
                disabled={!emailSent || loading.verify}
              >
                {loading.verify ? "확인 중..." : "확인"}
              </button>

              <label>소속 부서</label>
              <select
                name="department"
                value={form.department}
                onChange={onChange}
                required
              >
                <option value="">선택해주세요</option>
                <option value="DEVELOPMENT">개발부서</option>
                <option value="DESIGN">디자인부서</option>
                <option value="MARKETING">마케팅부서</option>
                <option value="SALES">총무과</option>
                <option value="HR">인사과</option>
                <option value="FINANCE">재무과</option>
              </select>

              <label>비밀번호</label>
              <input
                type="password"
                name="password"
                value={form.password}
                onChange={onChange}
                placeholder="비밀번호를 입력하세요"
                required
              />

              <label>비밀번호 확인</label>
              <input
                type="password"
                name="confirmPassword"
                value={form.confirmPassword}
                onChange={onChange}
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

              <button
                type="submit"
                className="submit-button"
                disabled={loading.submit}
              >
                {loading.submit ? "등록 중..." : "회원가입"}
              </button>
              <hr />
              <div className="loginLinks">
                <Link to="/login">이미 계정이 있으신가요? 로그인</Link>
              </div>
            </form>
          </div>
        </main>
      )}
    </Layout>
  );
}
