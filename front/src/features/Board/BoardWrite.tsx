import React, { useState, useEffect, FormEvent } from "react";
import axios from "axios";
import Layout from "../../components/Layout";
import { useNavigate, useLocation } from "react-router-dom";
import "./BoardWrite.css";
import { jwtDecode } from "jwt-decode";
import { predictBoard, PredictBoardResponse } from "../../api/ai";

const API_BASE = "http://localhost:8082"; // API 서버 주소

interface JwtPayload {
  role: string;
}

export default function BoardWrite() {
  const navigate = useNavigate();
  const location = useLocation();
  const editPost = location.state;
  const isEditMode = !!editPost?.id;

  const [title, setTitle] = useState(isEditMode ? editPost?.title || "" : "");
  const [content, setContent] = useState(isEditMode ? editPost?.content || "" : "");
  const [pinned, setPinned] = useState(false);
  const [department, setDepartment] = useState("");
  const [file, setFile] = useState<File | null>(null);
  const [loading, setLoading] = useState(false); // 🔹 전체 화면 로딩 상태
  const [submitting, setSubmitting] = useState(false); // ✅ 저장 중복 방지 상태

  const draftKey = isEditMode ? `editPostDraft-${editPost.id}` : "newPostDraft";

  useEffect(() => {
    const savedDraft = localStorage.getItem(draftKey);
    if (savedDraft) {
      const { title: savedTitle, content: savedContent } = JSON.parse(savedDraft);
      setTitle(savedTitle || "");
      setContent(savedContent || "");
    }
  }, [draftKey]);

  useEffect(() => {
    localStorage.setItem(draftKey, JSON.stringify({ title, content }));
  }, [title, content, draftKey]);

  const resetForm = () => {
    setTitle("");
    setContent("");
    setPinned(false);
    setDepartment("");
    setFile(null);
    localStorage.removeItem(draftKey);
  };

  const handleFileChange = (selectedFile: File | null) => {
    if (!selectedFile) return setFile(null);
    const validTypes = ["image/jpeg", "image/png", "image/jpg"];
    if (!validTypes.includes(selectedFile.type)) {
      alert("JPEG, JPG, PNG 형식의 파일만 가능합니다.");
      return;
    }
    if (selectedFile.size > 20 * 1024 * 1024) {
      alert("파일 크기는 20MB 이하만 가능합니다.");
      return;
    }
    setFile(selectedFile);
  };

  /** 🔹 AI 견적 생성 */
  const handleAiEstimate = async () => {
    if (!file) {
      alert("AI 분석을 위해 이미지를 업로드해주세요.");
      return;
    }
    try {
      setLoading(true);
      const result: PredictBoardResponse = await predictBoard(file);

      const formatted = result.detections
        .map(
          (det, idx) =>
            `시설물 ${idx + 1}:\n` +
            `- 클래스: ${det.class}\n` +
            `- 상태: ${det.status || "정보 없음"}\n` +
            `- 분석: ${det.vision_analysis || "정보 없음"}\n` +
            `- 견적: ${det.estimate !== null ? det.estimate + " 원" : "정보 없음"}\n` +
            `- 근거: ${det.estimate_basis || "정보 없음"}`
        )
        .join("\n\n");

      setContent((prev: string) => (prev ? prev + "\n\n" + formatted : formatted));
    } catch (error) {
      console.error("🚨 AI 분석 실패", error);
      alert("AI 분석 중 오류가 발생했습니다.");
    } finally {
      setLoading(false);
    }
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (submitting) return; // ✅ 이미 저장 중이면 무시
    setSubmitting(true); // ✅ 저장 시작

    if (!title.trim() || !content.trim()) {
      alert("제목과 내용을 입력해주세요.");
      setSubmitting(false); // ✅ 실패 시 다시 활성화
      return;
    }

    const token = localStorage.getItem("token");
    let postType = "FREE";
    if (token) {
      const decoded = jwtDecode<JwtPayload>(token);
      if (decoded.role === "ADMIN") postType = "NOTICE";
    }

    const dto = { type: postType, title, content };
    const formData = new FormData();
    formData.append("req", new Blob([JSON.stringify(dto)], { type: "application/json" }));
    if (file) formData.append("file", file);

    try {
      if (isEditMode) {
        await axios.put(`/api/boards/${editPost.id}`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${localStorage.getItem("token")}`
          }
        });
        alert("글이 수정되었습니다.");
      } else {
        await axios.post(`/api/boards`, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${localStorage.getItem("token")}`
          }
        });
        alert("글이 등록되었습니다.");
      }
      resetForm();
      navigate("/board");
    } catch (err) {
      console.error("저장 실패:", err);
      alert("저장에 실패했습니다.");
    } finally {
      setSubmitting(false); // ✅ 저장 완료 후 버튼 다시 활성화
    }
  };

  return (
    <Layout>
      {/* 🔹 로딩 오버레이 */}
      {loading && (
        <div style={overlayStyle}>
          <div style={spinnerStyle}></div>
          <p style={{ color: "#fff", marginTop: "10px", fontSize: "18px" }}>
            AI가 이미지를 분석 중입니다. 잠시만 기다려주세요...
          </p>
        </div>
      )}

      <div className="pw-container">
        <div className="pw-card">
          <h1 className="pw-heading">{isEditMode ? "글 수정" : "글 작성"}</h1>
          <form className="pw-form" onSubmit={handleSubmit}>
            <div className="pw-form-group">
              <label className="pw-label">제목 *</label>
              <input
                className="pw-input"
                placeholder="제목을 입력하세요"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                required
              />
            </div>

            <div className="pw-form-group">
              <label className="pw-label">내용 *</label>
              <textarea
                className="pw-textarea"
                placeholder="내용을 입력하세요"
                value={content}
                onChange={(e) => setContent(e.target.value)}
                required
              />
            </div>
            
            {/*파일 업로드 부분*/}
            <div className="pw-form-group">
              <label className="pw-label">파일 업로드</label>
              <div className="pw-file-upload">
                <input
                  type="file"
                  id="file-upload"
                  accept=".jpg,.jpeg,.png"
                  style={{ display: "none" }}
                  onChange={(e) => handleFileChange(e.target.files?.[0] ?? null)}
                />
                <label htmlFor="file-upload" className="pw-file-label">
                  파일 선택
                </label>

                {/* 파일명 + 삭제 X 버튼 */}
                {file ? (
                  <span className="pw-file-name" style={{ display: "inline-flex", alignItems: "center" }}>
                    {file.name}
                    <button
                      type="button"
                      onClick={() => setFile(null)}
                      style={{
                        marginLeft: "3px",
                        border: "none",
                        background: "transparent",
                        cursor: "pointer",
                        fontSize: "18px",
                        fontWeight: "bold",
                        color: "gray"
                      }}
                    >
                      ×
                    </button>
                  </span>
                ) : (
                  <span className="pw-file-name">선택된 파일 없음</span>
                )}
              </div>
              <small style={{ color: "red", display: "block", marginTop: "5px", fontSize: "12px", opacity: 0.8, fontWeight: "300" }}>
              20MB 이하 jpg, jpeg, png 파일만 업로드 가능합니다.
              </small>
            </div>

            {/* 🔹 AI 견적 생성 버튼 */}
            <div className="pw-form-group">
              <button
                type="button"
                className="pw-btn pw-btn-secondary"
                onClick={handleAiEstimate}
                disabled={loading}
                style={{ marginTop: "8px" }}
              >
                AI 견적 생성
              </button>
            </div>

            <div className="pw-actions">
              <button
                type="button"
                onClick={() => navigate("/board")}
                className="pw-btn pw-btn-cancel"
              >
                취소
              </button>
              <button
                type="submit"
                className="pw-btn pw-btn-primary"
                disabled={submitting} // ✅ 저장 중에는 비활성화
              >
                {submitting ? "저장" : "저장"} {/* ✅ 상태에 따라 텍스트 변경 */}
              </button>
            </div>
          </form>
        </div>
      </div>
    </Layout>
  );
}

/** 🔹 전체 화면 오버레이 스타일 */
const overlayStyle: React.CSSProperties = {
  position: "fixed",
  top: 0,
  left: 0,
  width: "100%",
  height: "100%",
  backgroundColor: "rgba(0, 0, 0, 0.6)",
  display: "flex",
  flexDirection: "column",
  justifyContent: "center",
  alignItems: "center",
  zIndex: 9999
};

/** 🔹 CSS 스피너 스타일 */
const spinnerStyle: React.CSSProperties = {
  border: "8px solid rgba(255, 255, 255, 0.3)",
  borderTop: "8px solid #fff",
  borderRadius: "50%",
  width: "60px",
  height: "60px",
  animation: "spin 1s linear infinite"
};
