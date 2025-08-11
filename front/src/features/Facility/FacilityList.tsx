// src/pages/FacilityList.tsx
import React, { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import "./FacilityList.css";
import { fetchFacilities, Facility } from "../../api/publicFa";

const FacilityList = () => {
  const [facilities, setFacilities] = useState<Facility[]>([]);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);

  useEffect(() => {
    fetchFacilities(0, 15)
        .then(data => setFacilities(data.content))
        .catch(err => console.error("공공시설물 목록 로드 실패:", err));
    }, []);


  const toggleSelect = (id: number) => {
    setSelectedIds((prev) =>
      prev.includes(id) ? prev.filter((sid) => sid !== id) : [...prev, id]
    );
  };

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
          />
          <button className="facility-advanced-btn">고급검색</button>
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
          {facilities.map((fa) => (
            <div
              key={fa.id}
              className={`facility-card-item ${
                selectedIds.includes(fa.id) ? "selected" : ""
              }`}
            >
              <input
                type="checkbox"
                checked={selectedIds.includes(fa.id)}
                onChange={() => toggleSelect(fa.id)}
                className="facility-checkbox"
              />
              <div className={`facility-status ${fa.status}`}>{fa.status}</div>
              <div className="facility-title">{fa.type}</div>
              <div className="facility-desc">{fa.section}</div>
              <div className="facility-period">{fa.installDate}</div>
            </div>
          ))}
        </div>

        {/* 페이지네이션 */}
        <div className="pagination">
          <button>이전</button>
          {[1, 2, 3, 4, 5].map((n) => (
            <button key={n}>{n}</button>
          ))}
          <button>다음</button>
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