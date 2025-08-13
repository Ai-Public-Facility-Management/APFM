// [기능 요약] 카드형 점검 리스트 페이지 (정부포털 톤)
import React, { useEffect, useState } from "react";
import Layout from "../../components/Layout";
import { useNavigate } from "react-router-dom";
import { fetchInspectionList, type InspectionSummary } from "../../api/inspection";
import type { PageResponse } from "../../types/paging";
import "./InspectionListPage.css";

export default function InspectionListPage() {
  const navigate = useNavigate();

  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [data, setData] = useState<PageResponse<InspectionSummary> | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);

  const load = async () => {
    setLoading(true);
    setErr(null);
    try {
      const res = await fetchInspectionList(page, size);
      setData(res);
    } catch (e: any) {
      setErr(e?.response?.data?.message ?? "리스트 불러오기에 실패했습니다.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { load(); }, [page]);

  return (
    <Layout>
      <div className="inspList-wrap">
        {loading && <div>불러오는 중…</div>}
        {err && <div className="inspList-error">{err}</div>}

        {!loading && !err && data && (
          <>
            <div className="inspList-list">
              {data.content.map((item: InspectionSummary) => ( // ← any 에러 해결
                <div
                  key={item.id}
                  className="inspList-card"
                  onClick={() => navigate(`/inspections/${item.id}`)}
                >
                  <div className="inspList-cardHeader">
                    <span className={
                      item.status.includes("완료")
                        ? "insp-status insp-status--done"
                        : "insp-status insp-status--writing"
                    }>
                      {item.status}
                    </span>

                    {item.hasReport && (
                      <span className="inspList-download" onClick={(e) => e.stopPropagation()}>
                        📄 보고서 다운로드
                      </span>
                    )}
                  </div>

                  <div className="inspList-title">
                    {item.createDate.split(" ")[0].replace(/\./g, " ")} 정기점검 &gt;
                  </div>

                  {item.hasIssue ? (
                    <>
                      <div>수리 필요 항목 {item.repairCount}건</div>
                      <div>철거 필요 항목 {item.removalCount}건</div>
                    </>
                  ) : (
                    <div>이상 없음</div>
                  )}

                  <div className="inspList-date">점검 일시 {item.createDate}</div>
                </div>
              ))}
            </div>

            <div className="inspList-pagination">
              <button disabled={data.first} onClick={() => setPage((p) => Math.max(0, p - 1))}>
                이전
              </button>
              {Array.from({ length: data.totalPages }, (_, i) => (
                <button
                  key={i}
                  className={i === data.number ? "active" : ""}
                  onClick={() => setPage(i)}
                >
                  {i + 1}
                </button>
              ))}
              <button disabled={data.last} onClick={() => setPage((p) => p + 1)}>
                다음
              </button>
            </div>
          </>
        )}
      </div>
    </Layout>
  );
}
