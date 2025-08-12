// src/pages/FacilityList.tsx
import React, { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import "./FacilityList.css";
import { fetchFacilities, Facility } from "../../api/publicFa";

const FacilityList = () => {
  const [facilities, setFacilities] = useState<Facility[]>([]);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [page, setPage] = useState(1);

  useEffect(() => {
    fetchFacilities(page - 1, 15)
      .then((data) => setFacilities(data.content))
      .catch((err) => console.error("공공시설물 목록 로드 실패:", err));
  }, [page]);

  const toggleSelect = (id: number) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((sid) => sid !== id) : [...prev, id]
    );
  };

  const getStatusLabel = (status: string, isProcessing: boolean) => {
    if (status === "NORMAL") {
      return { text: "정상", className: "status-normal" };
    }
    if (status === "ABNORMAL" && isProcessing) {
      return { text: "공사중", className: "status-processing" };
    }
    if (status === "ABNORMAL" && !isProcessing) {
      return { text: "수리 필요", className: "status-repair" };
    }
    return { text: "-", className: "" };
  };

  const [searchTerm, setSearchTerm] = useState("");

  const filteredFacilities = facilities.filter((fa) =>
    `${fa.cameraName} ${fa.publicFaId}번 ${fa.publicFaType} ${fa.condition}`
      .toLowerCase()
      .includes(searchTerm.toLowerCase())
  );

  return (
    <Layout>
      <div className="facility-page">
        {/* 페이지 제목 */}
        <h1 className="page-title">시설물 관리</h1>

        {/* 검색 및 필터 */}
        <div className="facility-search-filter">
          <input
            type="text"
            placeholder="검색어를 입력해주세요."
            className="facility-search-input"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
          <select className="facility-select">
            <option>종류</option>
            <option>전체</option>
            <option>공사중</option>
            <option>완료</option>
          </select>
          <select className="facility-select">
            <option>상태</option>
            <option>전체</option>
            <option>진행</option>
            <option>종료</option>
          </select>
        </div>

        {/* 카드 리스트 */}
        <div className="facility-card-grid">
          {filteredFacilities.map((fa) => {
            const label = getStatusLabel(fa.status, fa.isProcessing);
            return (
              <div
                key={`facility-${fa.publicFaId}`}
                className={`facility-card-item ${
                  selectedIds.includes(fa.publicFaId) ? "selected" : ""
                }`}
              >
                <div className="facility-card-header">
                  <div className="header-left">
                    <input
                      type="checkbox"
                      checked={selectedIds.includes(fa.publicFaId)}
                      onChange={() => toggleSelect(fa.publicFaId)}
                      className="facility-checkbox"
                    />
                  </div>
                  <div className="header-right">
                    <span className={`status-label ${label.className}`}>{label.text}</span>
                  </div>
                </div>
                <div className="facility-title">
                  {fa.cameraName} {fa.publicFaId}번 {fa.publicFaType}
                </div>
                <div className="facility-desc">{fa.condition}</div>
              </div>
            );
          })}
        </div>

        {/* 페이지네이션 */}
        <div className="pagination">
          <button
            onClick={() => setPage((p) => Math.max(1, p - 1))}
            disabled={page === 1}
          >
            이전
          </button>
          {[1, 2, 3, 4, 5].map((n) => (
            <button
              key={n}
              className={page === n ? "active" : ""}
              onClick={() => setPage(n)}
            >
              {n}
            </button>
          ))}
          <button onClick={() => setPage((p) => p + 1)}>다음</button>
        </div>

        {/* 선택 영역 */}
        <div className="facility-selection-bar">
          <span>{selectedIds.length}개 선택됨</span>
          <button className="facility-request-btn">제안 요청서 작성</button>
        </div>
      </div>
    </Layout>
  );
};

export default FacilityList;
