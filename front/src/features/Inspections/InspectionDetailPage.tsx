// [기능 요약] '점검 보고' 상세 페이지: 상단 안내배너 + 카메라별 섹션(이미지+이슈 목록) + 작성 버튼
import React, { useEffect, useMemo, useState } from "react";
import Layout from "../../components/Layout";
import { useParams, Link, useNavigate } from "react-router-dom";
import {
  fetchInspectionDetail,
  type InspectionDetail,
  type Camera,
  type IssueItem,
  generateInspectionReport
} from "../../api/inspection";
import { Backdrop, CircularProgress } from "@mui/material";
import "./InspectionDetailPage.css";

// [기능 요약] 라벨 + 값 표시
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

  // ✅ 보고서 생성 중 스피너 상태
  const [reportLoading, setReportLoading] = useState(false);
  const navigate = useNavigate();

  // 데이터 로딩
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

  // 안내 문구
  const noticeText = useMemo(() => {
    const date = data?.createDate ? data.createDate.split(" ")[0] : "-";
    // 모든 카메라의 이슈 개수 합산
    const count =
      data?.cameras?.reduce((sum, cam) => sum + (cam.issues?.length ?? 0), 0) ?? 0;
    return `${date} 정기점검 확인 요구 사항 ${count}건 있습니다.`;
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
            {data.cameras.map((camera: Camera, camIdx) => (
              <section key={camIdx} className="inspDetail-section">
                {/* 카메라 이름 */}
                <h2 className="inspDetail-sectionTitle">{camera.cameraName}</h2>

                {/* 카메라 이미지 */}
                {camera.imageUrl ? (
                  <img
                    className="inspDetail-mainImage"
                    src={camera.imageUrl}
                    alt={camera.cameraName}
                  />
                ) : (
                  <div className="inspDetail-imagePlaceholder">이미지 없음</div>
                )}

                {/* 카메라별 이슈 목록 */}
                {camera.issues && camera.issues.length > 0 ? (
                  camera.issues.map((issue: IssueItem) => (
                    <article key={issue.id} className="inspDetail-article">
                      <div className="inspDetail-fields">
                        <div className="inspDetail-fieldsCol">
                          <FieldLine label="시설물 종류" value={issue.publicFaType} />
                          <FieldLine label="이슈 타입" value={issue.type} />
                          <FieldLine
                            label="견적"
                            value={
                              issue.estimate
                                ? `${issue.estimate.toLocaleString()}원`
                                : "-"
                            }
                          />
                        </div>
                        <div className="inspDetail-fieldsCol">
                          <FieldLine
                            label="방해도"
                            value={
                              issue.obstruction !== undefined
                                ? `${issue.obstruction}`
                                : "-"
                            }
                          />
                          <FieldLine label="견적 근거" value={issue.estimateBasis} />
                        </div>
                      </div>
                    </article>
                  ))
                ) : (
                  <div>이 카메라에는 등록된 이슈가 없습니다.</div>
                )}
              </section>
            ))}

            {/* 하단 버튼 */}
            <div className="inspDetail-actions">
              <button
                className="inspDetail-primaryBtn"
                onClick={async () => {
                  if (!data) return;
                  try {
                    setReportLoading(true);
                    const inspectionId = data.id;
                    const issueIds = data.cameras.flatMap(c =>
                      c.issues.map(i => i.id)
                    );

                    const response = await generateInspectionReport(issueIds);

                    const blob = new Blob([response.data], { type: "application/pdf" });
                    const url = window.URL.createObjectURL(blob);

                    // 오늘 날짜 구하기
                    const today = new Date();
                    const year = today.getFullYear();
                    const month = String(today.getMonth() + 1).padStart(2, "0");
                    const day = String(today.getDate()).padStart(2, "0");

                    // 파일명: 2025-08-26_정기점검보고서.pdf
                    const fileName = `${year}-${month}-${day}_정기점검보고서.pdf`;

                    const link = document.createElement("a");
                    link.href = url;
                    link.setAttribute("download", fileName);
                    document.body.appendChild(link);
                    link.click();
                    link.remove();

                    // 👉 메모리 누수 방지
                    window.URL.revokeObjectURL(url);

                    alert(`${fileName} 가 다운로드되었습니다 `);

                    navigate("/inspections");
                  } catch (err) {
                    console.error(err);
                    alert("보고서 생성 중 오류가 발생했습니다.");
                  } finally {
                    setReportLoading(false);
                  }
                }}
              >
                정기 점검 보고서 작성
              </button>
            </div>


            {/* 리스트로 돌아가기 */}
            <div className="inspDetail-backRow">
              <Link to="/inspections" className="inspDetail-backLink">
                ← 리스트로
              </Link>
            </div>
          </>
        )}
        {/* ✅ 보고서 생성 중일 때 스피너 표시 */}
        <Backdrop open={reportLoading}
          sx={{
              color: "#fff",
              zIndex: 9999,
              display: "flex",
              flexDirection: "column", // 세로 정렬
              justifyContent: "center",
              alignItems: "center"
            }}>
          <CircularProgress color="inherit" />
           <div style={{ marginTop: "16px", fontSize: "18px", fontWeight: 500 }}>
              정기점검 보고서 생성중입니다. 잠시만 기다려 주세요.
           </div>
        </Backdrop>
      </div>
    </Layout>
  );
}
