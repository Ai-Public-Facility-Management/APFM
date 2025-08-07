// src/features/Login/LoginPage.tsx
import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import Layout from "../../components/Layout";
import "./LoginPage.css";

export default function LoginPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    email: "",
    password: "",
    rememberId: false,
  });

  // 아이디 저장 불러오기
  useEffect(() => {
    const savedEmail = localStorage.getItem("rememberedEmail");
    if (savedEmail) {
      setForm((prev) => ({
        ...prev,
        email: atob(savedEmail),
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
      const response = await axios.post(
        "/api/auth/login",
        {
          email: form.email,
          password: form.password,
        },
        { withCredentials: true }
      );
      // 응답 메시지에서 토큰만 분리
      const token = response.data.split("로그인 성공: ")[1];
      localStorage.setItem("token", token);

      // 아이디 저장 기능 처리
      if (form.rememberId) {
        localStorage.setItem("rememberedEmail", btoa(form.email));
      } else {
        localStorage.removeItem("rememberedEmail");
      }

      // 사용자에게 간단 메시지
      alert("로그인 성공!");
      navigate("/");
    } catch (error: any) {
      setForm((prev) => ({ ...prev, password: "" }));
      if (error.response) {
        alert("로그인에 실패했습니다. 이메일 또는 비밀번호를 확인하세요.");
      } else {
        alert("서버 오류: 로그인 요청 중 문제가 발생했습니다.");
      }
    }
  };

  return (
    <Layout>
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
          <Link to="/find-id">아이디 찾기</Link>
          <span>|</span>
          <Link to="/find-password">비밀번호 찾기</Link>
          <span>|</span>
          <Link to="/signup">회원가입</Link>
        </div>
        <hr className="loginHr" />
      </div>
    </Layout>
  );
}
