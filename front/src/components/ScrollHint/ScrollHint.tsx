// src/components/ScrollHint/ScrollHint.tsx
import React, { useEffect } from "react";
import { motion, useMotionValue, useTransform } from "framer-motion";
import "./ScrollHint.css";

export default function ScrollHint() {
  const yMV = useMotionValue(0);
  useEffect(() => {
    let raf = 0;
    const calc = () => { yMV.set(window.scrollY || 0); };
    const on = () => { cancelAnimationFrame(raf); raf = requestAnimationFrame(calc); };
    calc();
    window.addEventListener("scroll", on, { passive: true });
    window.addEventListener("resize", on);
    return () => { cancelAnimationFrame(raf); window.removeEventListener("scroll", on); window.removeEventListener("resize", on); };
  }, [yMV]);

  const opacity = useTransform(yMV, [0, 5, 25], [1, 0.85, 0]);
  const y = useTransform(yMV, [0, 25], [0, 8]);

  return (
    <div className="sh-wrap">
      <motion.div className="sh-inner" style={{ opacity, y }} role="note" aria-live="polite">
        <span className="sh-dot" aria-hidden>●</span>
        <span>아래로 스크롤하여 본 서비스의 주요 기능과 이용 방법을 확인해 주시기 바랍니다.</span>
        <span className="sh-chevrons" aria-hidden>
          <svg width="18" height="18" viewBox="0 0 24 24"><path d="M6 9l6 6 6-6" fill="none" stroke="currentColor" strokeWidth="2" /></svg>
          <svg width="18" height="18" viewBox="0 0 24 24" style={{ animationDelay: ".2s" }}><path d="M6 9l6 6 6-6" fill="none" stroke="currentColor" strokeWidth="2" /></svg>
        </span>
      </motion.div>
    </div>
  );
}
