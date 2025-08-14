import React, { useState, FormEvent } from "react";
import axios from "axios";
import Layout from "../../components/Layout";
import { useNavigate } from "react-router-dom";
import "./BoardWrite.css";

const API_BASE = "http://localhost:8082"; // API 서버 주소

export default function BoardWrite() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [pinned, setPinned] = useState(false);
  const [department, setDepartment] = useState("");
  const [file, setFile] = useState<File | null>(null);

  const navigate = useNavigate();

  const resetForm = () => {
    setTitle("");
    setContent("");
    setPinned(false);
    setDepartment("");
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
  /*
  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (!title.trim() || !content.trim()) {
      alert("제목과 내용을 입력해주세요.");
      return;
    }

    let attachments: { originalName: string; storedUrl: string }[] = [];

    if (file) {
      // 실제 파일 업로드 API가 있다면 업로드 후 URL을 받아야 함
      // 여기서는 예시로 storedUrl을 임시값으로 설정
      attachments.push({
        originalName: file.name,
        storedUrl: `/uploads/${file.name}`, // 실제 업로드 서버에서 받은 경로로 대체
      });
    }

    const payload = {
      type: "FREE", // 기본 게시판 타입 (백엔드 enum 값)
      title,
      content,
      pinned,
      department: department || null,
      attachments,
    };

    try {
      await axios.post(`${API_BASE}/api/boards`, payload, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("token")}`,
        },
      });

      alert("글이 등록되었습니다.");
      resetForm();
      navigate("/board"); // ✅ 저장 후 자동 이동
    } catch (err) {
      console.error("등록 실패:", err);
      alert("등록에 실패했습니다.");
    }*/
   const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    if (!title.trim() || !content.trim()) {
      alert("제목과 내용을 입력해주세요.");
      return;
    }

    const dto = {
      type: "FREE",
      title,
      content,
      pinned,
      department: department || null
    };

    const formData = new FormData();
    formData.append("data", new Blob([JSON.stringify(dto)], { type: "application/json" }));
    if (file) {
      formData.append("file", file);
    }

    try {
      await axios.post(`${API_BASE}/api/boards`, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
          Authorization: `Bearer ${localStorage.getItem("token")}`
        }
      });

      alert("글이 등록되었습니다.");
      resetForm();
      navigate("/board");
    } catch (err) {
      console.error("등록 실패:", err);
      alert("등록에 실패했습니다.");
    }
  };


  return (
    <Layout>
      <div className="pw-container">
        <div className="pw-card">
          <h1 className="pw-heading">글 작성</h1>
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
            
            <div className="pw-form-group">
              <label className="pw-label">파일 업로드</label>
              <div className="pw-file-upload">
                <input
                  type="file"
                  id="file-upload"
                  accept=".jpg,.jpeg,.png"
                  style={{ display: "none" }}
                  onChange={(e) =>
                    handleFileChange(e.target.files?.[0] ?? null)
                  }
                />
                <label htmlFor="file-upload" className="pw-file-label">
                  파일 선택
                </label>
                <span className="pw-file-name">
                  {file ? file.name : "선택된 파일 없음"}
                </span>
              </div>
            </div>

            <div className="pw-actions">
              <button
                type="button"
                onClick={resetForm}
                className="pw-btn pw-btn-cancel"
              >
                취소
              </button>
              <button type="submit" className="pw-btn pw-btn-primary">
                저장
              </button>
            </div>
          </form>
        </div>
      </div>
    </Layout>
  );
}
