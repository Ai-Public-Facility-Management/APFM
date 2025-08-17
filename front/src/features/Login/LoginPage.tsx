// src/features/Login/LoginPage.tsx
import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { motion, useMotionValue } from "framer-motion";
import Layout from "../../components/Layout";
import "./LoginPage.css";

import { loginAPI, saveToken, ID_KEY, clearEmail } from "../../api/login";

import ScrollHint from "../../components/ScrollHint/ScrollHint";
import { ServiceIntro } from "../../components/ServiceIntro/ServiceIntro";
import { FEATURE_STEPS, HOWTO_STEPS } from "../../content/serviceIntroData";

export default function LoginPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ email: "", password: "", rememberId: false });

  // 상단 진행바
  const progressMV = useMotionValue(0);
  useEffect(() => {
    let raf = 0;
    const calc = () => {
      const doc = document.documentElement, body = document.body;
      const y = window.pageYOffset || doc.scrollTop || body.scrollTop || 0;
      const sh = Math.max(body.scrollHeight, doc.scrollHeight, body.offsetHeight, doc.offsetHeight, body.clientHeight, doc.clientHeight);
      const vh = window.innerHeight || doc.clientHeight;
      const denom = Math.max(1, sh - vh);
      progressMV.set(Math.min(1, Math.max(0, y / denom)));
    };
    const on = () => { cancelAnimationFrame(raf); raf = requestAnimationFrame(calc); };
    calc();
    window.addEventListener("scroll", on, { passive: true });
    window.addEventListener("resize", on);
    window.addEventListener("load", calc);
    return () => { cancelAnimationFrame(raf); window.removeEventListener("scroll", on); window.removeEventListener("resize", on); window.removeEventListener("load", calc); };
  }, [progressMV]);

  // 아이디 저장 복원
  useEffect(() => {
    const saved = localStorage.getItem(ID_KEY);
    if (saved) {
      let decoded = saved; try { decoded = atob(saved); } catch {}
      setForm((p) => ({ ...p, email: decoded, rememberId: true }));
    }
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value, type, checked } = e.target;
    setForm((p) => ({ ...p, [name]: type === "checkbox" ? checked : value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const emailRegex = /^[\w-.]+@([\w-]+\.)+[\w-]{2,4}$/;
    if (!emailRegex.test(form.email)) { alert("올바른 이메일 형식을 입력하세요."); return; }
    try {
      const { token, userType, message } = await loginAPI(form);
      saveToken(token);
      form.rememberId ? localStorage.setItem(ID_KEY, btoa(form.email)) : clearEmail();
      alert(message);
      navigate(userType === "ADMIN" ? "/admin" : "/", { replace: true });
    } catch (error: any) {
      let errMsg = "로그인에 실패했습니다.";
      const data = error?.response?.data;
      if (typeof data === "string") { try { const p = JSON.parse(data); if (p.message) errMsg = p.message; } catch { errMsg = data; } }
      else if (typeof data === "object" && data?.message) errMsg = data.message;
      else if (error?.message) errMsg = error.message;
      alert(errMsg);
      setForm((p) => ({ ...p, password: "", email: p.rememberId ? p.email : "" }));
    }
  };

  return (
    <Layout mainClassName="loginMain">
      {/* 상단 진행바 */}
      <motion.div className="topProgress" style={{ scaleX: progressMV }} aria-hidden />

      {/* 로그인 박스 */}
      <div className="loginBox">
        <p className="loginSubTitle">공공시설물 관리자 로그인</p>
        <h1 className="loginTitle">아이디/비밀번호 로그인</h1>
        <hr className="loginHr" />
        <form onSubmit={handleSubmit} className="loginForm">
          <div className="formGroup">
            <label htmlFor="email">아이디</label>
            <input id="email" name="email" type="text" placeholder="아이디를 입력하세요"
                   value={form.email} onChange={handleChange} required />
          </div>
          <div className="formGroup">
            <label htmlFor="password">비밀번호</label>
            <input id="password" name="password" type="password" placeholder="비밀번호를 입력하세요"
                   value={form.password} onChange={handleChange} required />
          </div>
          <div className="formCheck">
            <input id="rememberId" name="rememberId" type="checkbox"
                   checked={form.rememberId} onChange={handleChange} />
            <label htmlFor="rememberId">아이디 저장</label>
          </div>
          <button type="submit" className="loginButton">로그인</button>
        </form>
        <div className="loginLinks">
          <Link to="/find-password">비밀번호 찾기</Link><span>|</span>
          <Link to="/signup">회원가입</Link>
        </div>
        <hr className="loginHr" />
      </div>

      {/* 안내문 */}
      <ScrollHint />

      {/* 소개(두 카테고리) */}
      <ServiceIntro featureSteps={FEATURE_STEPS} howtoSteps={HOWTO_STEPS} sectionGap={80} />
    </Layout>
  );
}
