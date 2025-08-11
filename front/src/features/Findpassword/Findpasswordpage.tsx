import React, { useEffect, useMemo, useRef, useState } from "react";
import Layout from "../../components/Layout";
import { requestResetCodeAPI, verifyResetCodeAPI, resetPasswordAPI } from "../../api/pw";

import "../../components/Common.css";
import "./Findpasswordpage.css";

export default function Findpasswordpage() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [code, setCode] = useState("");
  const [pw1, setPw1] = useState("");
  const [pw2, setPw2] = useState("");

  const [sending, setSending] = useState(false);
  const [codeSent, setCodeSent] = useState(false);
  const [codeVerified, setCodeVerified] = useState(false);
  const [loading, setLoading] = useState(false);

  const [msg, setMsg] = useState("");
  const [err, setErr] = useState("");

  const COOLDOWN_SEC = 60;
  const [cooldown, setCooldown] = useState(0);
  const timerRef = useRef<number | null>(null);

  const isEmailValid = useMemo(() => /\S+@\S+\.\S+/.test(email), [email]);
  const isCodeValid = useMemo(() => /^\d{6}$/.test(code.trim()), [code]);
  const isPwPolicyOk = useMemo(
    () => /^(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{10,}$/.test(pw1),
    [pw1]
  );
  const isPwSame = useMemo(() => pw1 !== "" && pw1 === pw2, [pw1, pw2]);

  useEffect(() => {
    if (cooldown <= 0 && timerRef.current) {
      clearInterval(timerRef.current);
      timerRef.current = null;
    }
  }, [cooldown]);

  const startCooldown = () => {
    setCooldown(COOLDOWN_SEC);
    if (timerRef.current) clearInterval(timerRef.current);
    timerRef.current = window.setInterval(() => setCooldown((c) => c - 1), 1000);
  };

  const clearAlerts = () => { setMsg(""); setErr(""); };

  const onSendCode = async () => {
    clearAlerts();
    if (!name.trim()) { setErr("이름을 입력해주세요."); return; }
    if (!isEmailValid) { setErr("올바른 이메일을 입력해주세요."); return; }
    try {
      setSending(true);
      await requestResetCodeAPI({ email });
      setCodeSent(true);
      setMsg("인증코드를 이메일로 발송했습니다. 메일함을 확인해주세요.");
      startCooldown();
    } catch {
      setErr("인증코드 발송에 실패했습니다. 잠시 후 다시 시도해주세요.");
    } finally {
      setSending(false);
    }
  };

  const onResend = async () => {
    clearAlerts();
    if (cooldown > 0) return;
    try {
      setLoading(true);
      await requestResetCodeAPI({ email });
      setCodeSent(true);
      setMsg("인증코드를 다시 발송했습니다.");
      startCooldown();
    } catch {
      setErr("코드 재전송에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const onVerifyCode = async () => {
    clearAlerts();
    if (!isEmailValid) { setErr("이메일을 확인해주세요."); return; }
    if (!isCodeValid) { setErr("6자리 숫자 코드를 입력하세요."); return; }
    try {
      setLoading(true);
      const { data } = await verifyResetCodeAPI({ email, code: code.trim() });
      if (data) {
        setCodeVerified(true);
        setMsg("코드 인증 완료! 새 비밀번호를 입력하세요.");
      } else {
        setErr("코드가 유효하지 않거나 만료되었습니다.");
      }
    } catch {
      setErr("코드 확인 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const onSubmitNewPassword = async () => {
    clearAlerts();
    if (!isEmailValid) { setErr("이메일을 확인해주세요."); return; }
    if (!isCodeValid) { setErr("6자리 숫자 코드를 입력하세요."); return; }
    if (!isPwPolicyOk) { setErr("비밀번호는 10자 이상이며 특수문자 1개 이상을 포함해야 합니다."); return; }
    if (!isPwSame) { setErr("비밀번호가 서로 일치하지 않습니다."); return; }

    try {
      setLoading(true);
      const { data } = await resetPasswordAPI({ email, code: code.trim(), password: pw1 });
      setMsg(data || "비밀번호 변경 완료!");
      setCodeVerified(false);
      setCodeSent(false);
      setName(""); setEmail(""); setCode(""); setPw1(""); setPw2("");
      setCooldown(0);
    } catch {
      setErr("비밀번호 변경에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const alertClass = err ? "is-error" : msg ? "is-info" : "is-empty";
  const alertText = err || msg || " ";

  return (
    <Layout>
      {/* 변수와 스타일은 컨테이너에 직접 선언된 값으로 강제 적용 */}
      <div className="fp-container">
        <h1 className="fp-title">비밀번호 찾기 / 재설정</h1>

        <div className={`fp-alert ${alertClass}`} role="status" aria-live="polite">
          {alertText}
        </div>

        <section className="fp-card">
          <h2 className="fp-subtitle">이름 / 이메일 확인</h2>

          <label className="fp-label" htmlFor="fp-name">아이디(이름)</label>
          <input
            id="fp-name"
            className="fp-input"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="홍길동"
            disabled={sending || loading || codeSent}
          />

          <label className="fp-label" htmlFor="fp-email">이메일</label>
          <input
            id="fp-email"
            className="fp-input"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="user@example.com"
            disabled={sending || loading || codeSent}
          />

          {!codeSent ? (
            <button
              className="fp-btn"
              onClick={onSendCode}
              disabled={sending || !name.trim() || !isEmailValid}
            >
              {sending ? "발송 중..." : "인증코드 발송"}
            </button>
          ) : (
            <>
              <button className="fp-btn" disabled>인증코드 발송 완료</button>
              <button
                className="fp-btn fp-btn-ghost"
                onClick={onResend}
                disabled={loading || cooldown > 0}
                title={cooldown > 0 ? `재전송까지 ${cooldown}s` : "코드 재전송"}
              >
                재전송{cooldown > 0 ? `(${cooldown}s)` : ""}
              </button>
            </>
          )}
        </section>

        <section className="fp-card">
          <h2 className="fp-subtitle">인증코드 확인</h2>

          <label className="fp-label" htmlFor="fp-code">인증코드</label>
          <input
            id="fp-code"
            className="fp-input"
            type="text"
            value={code}
            maxLength={6}
            onChange={(e) => setCode(e.target.value.replace(/\D/g, ""))}
            placeholder="123456"
            disabled={!codeSent || codeVerified}
          />

          <button
            className="fp-btn"
            onClick={onVerifyCode}
            disabled={!codeSent || codeVerified || loading || !isCodeValid}
          >
            {codeVerified ? "코드 인증 완료" : "코드 인증"}
          </button>
        </section>

        <section className={`fp-card ${codeVerified ? "" : "fp-disabled"}`}>
          <h2 className="fp-subtitle">새 비밀번호 설정</h2>

          <label className="fp-label" htmlFor="fp-pw1">새 비밀번호</label>
          <input
            id="fp-pw1"
            className="fp-input"
            type="password"
            value={pw1}
            onChange={(e) => setPw1(e.target.value)}
            disabled={!codeVerified}
            autoComplete="new-password"
          />

          <label className="fp-label" htmlFor="fp-pw2">새 비밀번호 확인</label>
          <input
            id="fp-pw2"
            className="fp-input"
            type="password"
            value={pw2}
            onChange={(e) => setPw2(e.target.value)}
            disabled={!codeVerified}
            autoComplete="new-password"
          />

          <div className={`fp-pwcheck ${!pw1 && !pw2 ? "empty" : pw1 === pw2 ? "match" : "warn"}`}>
            {pw1 && pw2 ? (pw1 === pw2 ? "비밀번호가 일치합니다." : "동일한 비밀번호를 입력하세요") : " "}
          </div>

          <button
            className="fp-btn"
            onClick={onSubmitNewPassword}
            disabled={!codeVerified || loading || !isPwPolicyOk || !isPwSame}
          >
            비밀번호 변경
          </button>

          <ul className="fp-policy">
            <li className={isPwPolicyOk ? "ok" : ""}>10자 이상 + 특수문자 1개 이상</li>
          </ul>
        </section>
      </div>
    </Layout>
  );
}
