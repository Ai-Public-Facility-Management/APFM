// [기능 요약] '점검 보고' 상세 페이지: 상단 안내배너 + 카메라별 섹션(이미지+이슈 목록) + 작성 버튼
import React, { useEffect, useMemo, useState } from "react";
import Layout from "../../components/Layout";
import { useParams, Link, useNavigate } from "react-router-dom";
import {
  fetchInspectionDetail,
  type InspectionDetail,
  type Camera,
  type IssueItem,
  generateInspectionReport,
  downloadInspectionReport
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
    return `${date} 정기점검 확인 요구 사항 ${count}건 있습니다요.`;
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
              {/* 작성 버튼 (보고서 없을 때만 표시) */}
              {data.status !== "작성 완료" && (
                <button
                  className="inspDetail-primaryBtn"
                  disabled={reportLoading}
                  onClick={async () => {
                    if (!data) return;
                    try {
                      setReportLoading(true);
                      const inspectionId = data.id;
                      const issueIds = data.cameras.flatMap(c => c.issues.map(i => i.id));

                      // 보고서 생성 요청 (PDF 받아오지만 지금은 저장 안 하고 상태만 갱신)
                      await generateInspectionReport(inspectionId, issueIds);

                      alert("보고서가 저장되었습니다 ✅");

                      // 상태 갱신
                      const refreshed = await fetchInspectionDetail(inspectionId);
                      setData(refreshed);
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
              )}

              {/* 다운로드 버튼 (보고서가 있을 때만 표시) */}
              {data.status === "작성 완료" && (
                <button
                  className="inspDetail-secondaryBtn"
                  onClick={async () => {
                    try {
                      if (!data) return;
                      const response = await downloadInspectionReport(data.id); // ✅ 변경됨

                      const blob = new Blob([response.data], { type: "application/pdf" });
                      const url = window.URL.createObjectURL(blob);

                      const today = new Date();
                      const fileName = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, "0")}-${String(today.getDate()).padStart(2, "0")}_정기점검보고서.pdf`;

                      const link = document.createElement("a");
                      link.href = url;
                      link.setAttribute("download", fileName);
                      document.body.appendChild(link);
                      link.click();
                      link.remove();

                      window.URL.revokeObjectURL(url);

                      alert(`${fileName} 다운로드 완료 ✅`);
                    } catch (err) {
                      console.error(err);
                      alert("보고서 다운로드 중 오류가 발생했습니다.");
                    }
                  }}
                >
                  보고서 다운로드
                </button>
              )}
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
