import React, { useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";
import "./Common.css";

interface Props {
  onClose: () => void;
  clauseName?: string; // 예: "termsOfUse", "privacyNotice"
}

export default function TermsModalForFooter({ onClose, clauseName = "" }: Props) {
  const [markdown, setMarkdown] = useState("");
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const loadMarkdown = async () => {
      if (!clauseName) return;

      setLoading(true);

      try {
        // ✅ Webpack/Vite에서 지원하는 동적 import (단, 경로는 미리 포함되어 있어야 함)
        const res = await import(`../assets/terms/${clauseName}.md`);
        const text = await fetch(res.default).then((r) => r.text());
        setMarkdown(text);
      } catch (error) {
        setMarkdown("약관 내용을 불러오는 데 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };

    loadMarkdown();
  }, [clauseName]);

  return (
    <div className="modal-backdrop">
      <div className="modal-content">
        <h2>약관 상세 보기</h2>
        <div className="markdown-box">
          {loading ? (
            <div className="spinner-wrapper">
              <div className="spinner" />
            </div>
          ) : (
            <ReactMarkdown>{markdown}</ReactMarkdown>
          )}
        </div>
        <button className="modal-button" onClick={onClose}>닫기</button>
      </div>
    </div>
  );
}
