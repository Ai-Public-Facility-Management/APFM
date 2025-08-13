import React, { useState, FormEvent } from "react";
import axios from "axios";
import Layout from "../../components/Layout";
import "./BoardWrite.css";

const API_BASE = "http://localhost:8082"; // 실제 API 서버 주소

export default function BoardWrite() {
  const [title, setTitle] = useState("");
  const [summary, setSummary] = useState("");
  const [file, setFile] = useState<File | null>(null);

  const resetForm = () => {
    setTitle("");
    setSummary("");
    setFile(null);
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

  const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (!title.trim() || !summary.trim()) {
      alert("제목과 내용을 입력해주세요.");
      return;
    }

    const formData = new FormData();
    formData.append("title", title);
    formData.append("summary", summary);
    if (file) formData.append("file", file);

    axios
      .post(`${API_BASE}/api/boards`, formData, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("token")}`,
          "Content-Type": "multipart/form-data"
        }
      })
      .then(() => {
        alert("글이 등록되었습니다.");
        resetForm();
      })
      .catch((err) => {
        console.error("등록 실패:", err);
        alert("등록에 실패했습니다.");
      });
  };

  return (
    <Layout>
      <div className="pw-container">
        <h1 className="pw-heading">프로젝트 글 작성</h1>
        <form className="pw-form" onSubmit={handleSubmit}>
          <div className="pw-form-group">
            <label className="pw-label">제목 *</label>
            <input className="pw-input" value={title} onChange={(e) => setTitle(e.target.value)} required />
          </div>
          <div className="pw-form-group">
            <label className="pw-label">내용 *</label>
            <textarea className="pw-textarea" value={summary} onChange={(e) => setSummary(e.target.value)} required />
          </div>
          <div className="pw-form-group">
            <label className="pw-label">파일 업로드</label>
            <input type="file" accept=".jpg,.jpeg,.png" onChange={(e) => handleFileChange(e.target.files?.[0] ?? null)} />
          </div>
          <div className="pw-actions">
            <button type="button" onClick={resetForm} className="pw-btn">취소</button>
            <button type="submit" className="pw-btn pw-btn-primary">저장</button>
          </div>
        </form>
      </div>
    </Layout>
  );
}