// [기능 요약] '점검 보고' 상세 페이지: 상단 안내배너 + 섹션 카드(이미지+항목) + 작성 버튼
import React, { useEffect, useMemo, useState } from "react";
import Layout from "../../components/Layout";
import { useParams, Link } from "react-router-dom";
import { fetchInspectionDetail, type InspectionDetail } from "../../api/inspection";
import "./InspectionDetailPage.css";

// ⚠️ FastAPI 이미지 서버 베이스 URL
// - .env 에 REACT_APP_FASTAPI_BASE 가 있으면 사용, 없으면 임시값 사용
const FASTAPI_BASE =
  process.env.REACT_APP_FASTAPI_BASE ?? "http://localhost:8000";

// [기능 요약] 이슈에 연결된 AI 이미지 URL을 만들어주는 유틸 (필요 시 수정)
// - 백엔드/파스트API 실제 경로에 맞춰 조정하세요.
function getAiImageUrl(issue: any) {
  // 우선순위: 백엔드가 직접 내려주는 imageUrl → 없으면 FastAPI 경로 조합
  if (issue?.imageUrl) return issue.imageUrl;
  if (issue?.aiImagePath) return `${FASTAPI_BASE}${issue.aiImagePath}`;
  // ex) /estimate/images/{issueId} 같은 형태를 쓰는 경우
  if (issue?.id) return `${FASTAPI_BASE}/estimate/images/${issue.id}`;
  return ""; // 이미지 없으면 빈 문자열
}

// [기능 요약] 상세 항목을 '라벨: 값' 리스트로 그린다
function FieldLine({ label, value }: { label: string; value?: React.ReactNode }) {
  return (
    <div className="inspDetail-line">
      <span className="inspDetail-lineLabel">{label}</span>
      <span className="inspDetail-lineValue">{value ?? "-"}</span>
    </div>
  );
}

export default function InspectionDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [data, setData] = useState<InspectionDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);

  // [기능 요약] 상세 데이터 로딩
  useEffect(() => {
    const load = async () => {
      if (!id) return;
      setLoading(true);
      setErr(null);
      try {
        const res = await fetchInspectionDetail(Number(id));
        setData(res);
      } catch (e: any) {
        setErr(e?.response?.data?.message ?? "상세 불러오기에 실패했습니다.");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [id]);

  // [기능 요약] 이슈를 시설 카테고리(예: 가로등, 벤치 등)로 묶어 UI 섹션 생성
  // - 백엔드에서 issue.type / issue.facilityName 등을 기준으로 그룹핑
  const grouped = useMemo(() => {
    const map = new Map<string, any[]>();
    (data?.issues ?? []).forEach((it) => {
      const key =
        it?.facilityCategory ||
        it?.type || // ex) "가로등", "벤치"
        "기타";
      const arr = map.get(key) ?? [];
      arr.push(it);
      map.set(key, arr);
    });
    return Array.from(map.entries()); // [ [카테고리, 이슈배열], ... ]
  }, [data]);

  // [기능 요약] 상단 안내 문구 (원하는 형식으로 가공)
  const noticeText = useMemo(() => {
    const date = data?.createDate ? data.createDate.split(" ")[0] : "-";
    const count = data?.issues?.length ?? 0;
    return `${date} 정기점검 확인 요규 사항 ${count}건 있습니다.`.replace("요규", "요구");
  }, [data]);

  return (
    <Layout>
      <div className="inspDetail-wrap">
        <h1 className="inspDetail-pageTitle">점검 보고</h1>

        {/* 안내 배너 */}
        <div className="inspDetail-banner">{noticeText}</div>

        {loading && <div>불러오는 중…</div>}
        {err && <div className="inspDetail-error">{err}</div>}

        {!loading && !err && data && (
          <>
            {/* 섹션들 (카테고리별) */}
            {grouped.map(([category, issues]) => (
              <section key={category} className="inspDetail-section">
                <h2 className="inspDetail-sectionTitle">{category}</h2>

                {issues.map((issue) => {
                  const img = getAiImageUrl(issue);
                  return (
                    <article key={issue.id} className="inspDetail-article">
                      {/* 큰 이미지 */}
                      {img ? (
                        <img
                          className="inspDetail-mainImage"
                          src={img}
                          alt={`${category}-${issue.id}`}
                        />
                      ) : (
                        // 이미지가 아직 준비되지 않았다면 비워두거나 로더/플레이스홀더 표시
                        <div className="inspDetail-imagePlaceholder">
                          {/* TODO: FastAPI 이미지 경로 확정 후 연결하세요 */}
                          이미지 준비 중
                        </div>
                      )}

                      {/* 항목 리스트 (좌/우 2열 느낌) */}
                      <div className="inspDetail-fields">
                        <div className="inspDetail-fieldsCol">
                          <FieldLine label="카메라" value={data.location ?? data.facilityName ?? "-"} />
                          <FieldLine label="상태" value={issue.status} />
                          <FieldLine label="발생도" value={issue.severity ?? issue.level ?? issue.count} />
                          <FieldLine label="확인 요구 사항" value={issue.description ?? issue.content} />
                          <FieldLine label="수리 견적" value={issue.estimate ? `${issue.estimate.toLocaleString()}원` : "-"} />
                        </div>
                        <div className="inspDetail-fieldsCol">
                          <FieldLine label="산출 근거" value={
                            <div className="inspDetail-prewrap">
                              {issue.estimateBasis ??
                                "공사 필요 구간, 장비/인력 소요, 공사 기간, 자재 등 상세 근거"}
                            </div>
                          } />
                          {/* 필요 시 항목 추가: 예) 적발 근거 이미지 수, 좌표 등 */}
                        </div>
                      </div>
                    </article>
                  );
                })}
              </section>
            ))}

            {/* 하단 버튼 */}
            <div className="inspDetail-actions">
              <button
                className="inspDetail-primaryBtn"
                onClick={() => {
                  // TODO: 보고서 작성 페이지 라우팅 or 모달 오픈
                  // e.g., navigate(`/reports/new?inspectionId=${id}`)
                  alert("정기 점검 보고서 작성 기능은 추후 연결해주세요.");
                }}
              >
                정기 점검 보고서 작성
              </button>
            </div>

            {/* 리스트로 돌아가기 링크 (원하면 제거 가능) */}
            <div className="inspDetail-backRow">
              <Link to="/inspections" className="inspDetail-backLink">← 리스트로</Link>
            </div>
          </>
        )}
      </div>
    </Layout>
  );
}
