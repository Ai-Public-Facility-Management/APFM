// src/components/ScrollHint/ScrollHint.tsx
import React from "react";
import { motion } from "framer-motion";
import "./ScrollHint.css";

const EASE: [number, number, number, number] = [0.22, 1, 0.36, 1];

export default function ScrollHint() {
  return (
    <div className="sh-wrap">
      {/* 초기 한 번만 부드럽게 나타나고, 스크롤과는 무관 */}
      <motion.div
        className="sh-inner"
        role="note"
        aria-live="polite"
        initial={{ opacity: 0, y: 4 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.28, ease: EASE }}
      >
        <span className="sh-dot" aria-hidden>●</span>
        <span>
          아래로 스크롤하여 본 서비스의 주요 기능과 이용 방법을 확인해 주시기 바랍니다.
        </span>
        <span className="sh-chevrons" aria-hidden>
          <svg width="18" height="18" viewBox="0 0 24 24">
            <path d="M6 9l6 6 6-6" fill="none" stroke="currentColor" strokeWidth="2" />
          </svg>
          <svg width="18" height="18" viewBox="0 0 24 24" style={{ animationDelay: ".2s" }}>
            <path d="M6 9l6 6 6-6" fill="none" stroke="currentColor" strokeWidth="2" />
          </svg>
        </span>
      </motion.div>
    </div>
  );
}
