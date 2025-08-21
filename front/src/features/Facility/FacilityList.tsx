// src/pages/FacilityList.tsx
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import "./FacilityList.css";
import { fetchFacilities, Facility, createProposal } from "../../api/publicFa";
import {Backdrop, CircularProgress} from "@mui/material";


const FacilityList = () => {
  const [facilities, setFacilities] = useState<Facility[]>([]);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [loading, setLoading] = useState(false);
  const [statusFilter, setStatusFilter] = useState("전체");
  const [conditionFilter, setConditionFilter] = useState("전체");




  useEffect(() => {
    fetchFacilities(page - 1, 16)
        .then((data) => {setFacilities(data.content);
          setTotalPages(data.totalPages);
        })
        .catch((err) => console.error("공공시설물 목록 로드 실패:", err));
  }, [page]);

  const toggleSelect = (id: number, status: string, processing: boolean) => {
    const label = getStatusLabel(status, processing);
    if (label.text === "정상") return; // 정상은 선택 불가

    setSelectedIds((prev) =>
        prev.includes(id) ? prev.filter((sid) => sid !== id) : [...prev, id]
    );
  };

  const getStatusLabel = (status: string, processing: boolean) => {
    if (status === "NORMAL") {
      return { text: "정상", className: "status-normal" };
    }
    if (status === "ABNORMAL" && processing) {
      return { text: "공사중", className: "status-processing" };
    }
    if (status === "ABNORMAL" && !processing) {
      return { text: "수리 필요", className: "status-repair" };
    }
    return { text: "-", className: "" };
  };


  const [searchTerm, setSearchTerm] = useState("");

  const filteredFacilities = facilities.filter((fa) => {
    const label = getStatusLabel(fa.status, fa.processing);

    const matchSearch = `${fa.cameraName} ${fa.publicFaId}번 ${fa.publicFaType} ${fa.condition}`
        .toLowerCase()
        .includes(searchTerm.toLowerCase());

    const matchStatus =
        statusFilter === "전체" || label.text === statusFilter;

    const matchCondition =
        conditionFilter === "전체" || fa.condition === conditionFilter;

    return matchSearch && matchStatus && matchCondition;
  });


  const handleProposalRequest = async () => {
    try {
      setLoading(true); // ✅ 로딩 시작
      await createProposal(selectedIds); // ✅ API 호출
      navigate("/proposal", { state: { ids: selectedIds } }); // ✅ 이동 + 선택 ID 전달
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const navigate = useNavigate();

  return (
      <Layout>
        {/* 로딩 화면 */}
        <Backdrop open={loading} sx={{ color: "#fff", zIndex: (theme) => theme.zIndex.drawer + 1 }}>
          <CircularProgress color="inherit" />
        </Backdrop>

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
            <select
                className="facility-select"
                value={statusFilter}
                onChange={(e) => setStatusFilter(e.target.value)}
            >
              <option>전체</option>
              <option>공사중</option>
              <option>수리 필요</option>
              <option>정상</option>
            </select>

            <select
                className="facility-select"
                value={conditionFilter}
                onChange={(e) => setConditionFilter(e.target.value)}
            >
              <option>전체</option>
              <option>정상</option>
              <option>표면 벗겨짐</option>
              <option>파손</option>
              <option>변형</option>
              <option>변색</option>
              <option>균열</option>
            </select>

          </div>

          {/* 카드 리스트 */}
          <div className="facility-card-grid">
            {filteredFacilities.map((fa) => {
              const label = getStatusLabel(fa.status, fa.processing);
              return (
                  <div
                      key={`facility-${fa.publicFaId}`}
                      className={`facility-card-item ${
                          selectedIds.includes(fa.publicFaId) ? "selected" : ""}`}
                      onClick={() => navigate(`/detail/${fa.publicFaId}`)}
                      style={{ cursor: "pointer" }}
                  >
                    <div className="facility-card-header">
                      <div className="header-left">
                        <input
                            type="checkbox"
                            checked={selectedIds.includes(fa.publicFaId)}
                            disabled={label.text === "정상"}
                            onClick={e => e.stopPropagation()}
                            onChange={() => toggleSelect(fa.publicFaId, fa.status, fa.processing)}
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

            {Array.from({ length: totalPages }, (_, i) => i + 1)
                .filter((n) => {
                  // 최대 5개 버튼만 보여주고 현재 페이지 중심으로 슬라이딩
                  const start = Math.max(1, Math.min(page - 2, totalPages - 4));
                  return n >= start && n <= start + 4;
                })
                .map((n) => (
                    <button
                        key={n}
                        className={page === n ? "active" : ""}
                        onClick={() => setPage(n)}
                    >
                      {n}
                    </button>
                ))}

            <button
                onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
                disabled={page === totalPages}
            >
              다음
            </button>
          </div>



          {/* 선택 영역 */}
          <div className="facility-selection-bar">
            <span>{selectedIds.length}개 선택됨</span>
            <button className="facility-request-btn" onClick={handleProposalRequest}>제안 요청서 작성</button>
          </div>
        </div>
      </Layout>
  );
};

export default FacilityList;