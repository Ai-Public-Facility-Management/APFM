// src/features/Login/LoginPage.tsx
import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import "./LoginPage.css";

import {
  loginAPI,
  saveToken,
  ID_KEY,
  clearEmail,
} from "../../api/login";

export default function LoginPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    email: "",
    password: "",
    rememberId: false,
  });

  // 아이디 저장 불러오기 (기존 base64 방식 호환)
  useEffect(() => {
    const saved = localStorage.getItem(ID_KEY);
    if (saved) {
      let decoded = saved;
      try { decoded = atob(saved); } catch {}
      setForm((prev) => ({
        ...prev,
        email: decoded,
        rememberId: true,
      }));
    }
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setForm((prev) => ({
      ...prev,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    // 이메일 유효성 검사
    const emailRegex = /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/;
    if (!emailRegex.test(form.email)) {
      alert("올바른 이메일 형식을 입력하세요.");
      return;
    }

    try {
      const { token, userType, message } = await loginAPI(form);
      saveToken(token);

      // 아이디 저장
      if (form.rememberId) {
        localStorage.setItem(ID_KEY, btoa(form.email));
      } else {
        clearEmail();
      }

      alert(message); // "관리자로 로그인되었습니다." 또는 "로그인 성공."
      navigate(userType === "ADMIN" ? "/admin" : "/", { replace: true });
    } catch (error: any) {
      console.error("로그인 실패:", error);

      let errMsg = "로그인에 실패했습니다.";

      const data = error?.response?.data;

      if (typeof data === "string") {
        // 문자열이면 JSON 파싱 시도
        try {
          const parsed = JSON.parse(data);
          if (parsed.message) {
            errMsg = parsed.message;
          }
        } catch {
          errMsg = data; // 그냥 일반 문자열이면 그대로 사용
        }
      } else if (typeof data === "object" && data?.message) {
        // JSON 객체면 바로 사용
        errMsg = data.message;
      } else if (error?.message) {
        errMsg = error.message;
      }

      alert(errMsg);

      setForm((prev) => ({
        ...prev,
        password: "",
        email: prev.rememberId ? prev.email : "",
      }));
    }
  };

  return (
    <Layout mainClassName="loginMain">
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
          <button type="submit" className="loginButton">로그인</button>
        </form>
        <div className="loginLinks">
          <Link to="/find-password">비밀번호 찾기</Link>
          <span>|</span>
          <Link to="/signup">회원가입</Link>
        </div>
        <hr className="loginHr" />
      </div>
    </Layout>
  );
}
