import Layout from "../../components/Layout";
import { useParams, Link } from "react-router-dom";
import "./DetailPublicFa.css";
import { fetchFacilityDetail, FacilityDetail } from "../../api/publicFa";
import React, { useEffect, useState, useRef } from "react";
import {uploadResult} from "../../api/upload";


export default function DetailPublicFa(){
    const { id } = useParams<{ id: string }>();
    const [detail, setDetail] = useState<FacilityDetail | null>(null);

    // 결과 보고서 상태
    type ReportRow = {
        id: string;
        file: File;
        name: string;
        ext: string;
        sizeLabel: string;
        status: "uploading" | "done"; // 업로드 상태
    };
    const [isDragging, setDragging] = useState(false);
    const [reports, setReports] = useState<ReportRow[]>([]);
    const inputRef = useRef<HTMLInputElement>(null);

    const humanSize = (bytes: number) =>
        bytes < 1024 ? `${bytes}B`
            : bytes < 1024 * 1024 ? `${(bytes/1024).toFixed(1)}KB`
                : `${(bytes/1024/1024).toFixed(1)}MB`;

    const pickExt = (name: string) => {
        const i = name.lastIndexOf(".");
        return i >= 0 ? name.slice(i).toLowerCase() : "";
    };

    const ACCEPT_EXT = [".hwp", ".docx"];      // 필요시 수정
    const MAX_SIZE = 20 * 1024 * 1024;        // 20MB
    const MAX_COUNT = 5;

    function simulateUpload(id: string) {
        // 1~2.5초 사이 랜덤 시간 후 완료 처리
        const duration = 1000 + Math.floor(Math.random() * 1500);
        setTimeout(() => {
            setReports(prev => prev.map(r => (r.id === id ? { ...r, status: "done" } : r)));
        }, duration);
    }

    function addFile(file: File | null) {
        if (!file) return;

        const errs: string[] = [];
        const ext = pickExt(file.name);

        if (!ACCEPT_EXT.includes(ext)) {
            errs.push(`${file.name}: 형식 미허용(${ext})`);
        } else if (file.size > MAX_SIZE) {
            errs.push(`${file.name}: 용량 초과(${humanSize(MAX_SIZE)} 이하)`);
        }

        if (errs.length) {
            alert(errs.join("\n"));
            return;
        }

        // 기존 업로드 제거하고 새 파일만 유지 (단일 파일 전용)
        const newRow = {
            id: Math.random().toString(36).substring(2, 10) + Date.now().toString(36),
            file: file,
            name: file.name.replace(/\.[^/.]+$/, ""),
            ext: ext,
            sizeLabel: humanSize(file.size),
            status: "uploading" as const,
            progress: 0
        };

        setReports([newRow]); // 항상 하나만 저장

        // 업로드 시작
        uploadResult(file, Number(id), (percent) => {
            setReports(prev =>
                prev.length ? [{ ...prev[0], progress: percent }] : prev
            );
        })
            .then(() => {
                setReports(prev =>
                    prev.map(r =>
                        r.id === newRow.id
                            ? { ...r, status: "done", progress: 100 }
                            : r
                    )
                );
                setDetail(prev => prev ? { ...prev, hasReport: true } : prev);
            })
            .catch(() => {
                alert(`${newRow.name} 업로드 실패`);
                setReports([]); // 실패 시 비움
            });
    }

    const onDragOver  = (e: React.DragEvent) => { e.preventDefault(); setDragging(true); };
    const onDragEnter = (e: React.DragEvent) => { e.preventDefault(); setDragging(true); };
    const onDragLeave = () => setDragging(false);
    const onDrop = (e: React.DragEvent) => {
        e.preventDefault();
        setDragging(false);
        if (e.dataTransfer.files && e.dataTransfer.files[0]) {
            addFile(e.dataTransfer.files[0]);
        }
    };
    const onBrowseClick = () => inputRef.current?.click();
    const onInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            addFile(e.target.files[0]);
        }
        e.target.value = ""; // 같은 파일 재선택 가능하도록 초기화
    };

    const removeOne = (id: string) => {
        setReports(prev => prev.filter(r => r.id !== id));
    };

    useEffect(() => {
    if (id) {
      fetchFacilityDetail(Number(id))
        .then((data) => setDetail(data))
        .catch((err) => console.error("상세 조회 실패:", err));
    }
  }, [id]);

  if (!detail) return <Layout mainClassName="detailMain">로딩 중...</Layout>;


    return (
        <Layout mainClassName="detailMain">
            <div className="detail-header">
                <h1>{detail.cameraName}</h1>
                <Link to='/facility-list'  className="back-to-list-btn">
                    ← 리스트로
                </Link>
            </div>
        <hr className="custom-hr" />
            <div className="detail-content">
                <table className="detail-table">
                <tbody>
                    <tr>
                        <th>종류</th>
                        <td>{detail.type}</td>
                    </tr>
                    <tr>
                        <th>상태</th>
                        <td>{detail.status}</td>
                    </tr>
                    <tr>
                        <th>설치 날짜</th>
                        <td>{detail.installDate}</td>
                    </tr>
                    <tr>
                        <th>마지막 수리 날짜</th>
                        <td>{detail.lastRepair}</td>
                    </tr>
                    <tr>
                        <th>차단 정도</th>
                        <td>{detail.obstruction}</td>
                    </tr>
                    <tr>
                        <th>방해도</th>
                        <td style={{ whiteSpace: "pre-line" }}>
                            {detail.obstruction_basis?.replace(/\\n/g, "\n")}
                        </td>
                    </tr>
                    <tr>
                        <th>예상 견적</th>
                        <p>{detail.estimate?.toLocaleString?.() ?? "견적 없음"}</p>
                    </tr>
                    <tr>
                        <th>견적 근거</th>
                        <td style={{ whiteSpace: "pre-line" }}>
                            {detail.estimateBasis ? detail.estimateBasis.replace(/\\n/g, "\n") : ""}
                        </td>
                    </tr>
                </tbody>
            </table>

                <div className="detail-image">
                    {detail.image ? (
                        <img src={detail.image} alt={"이미지"} />
                    ) : (
                        <p>이미지 없음</p>
                    )}
                </div>
            </div>
            <hr className="custom-hr-2"/>
            <section className="report-section">
                <h1 className="report-title">결과 보고서</h1>
                {detail.hasReport ? (
                    // 이미 보고서 있는 경우
                    <div
                        className="dropzone already-exists"
                        role="alert"
                        aria-label="결과 보고서 존재 안내"
                    >
                        <p className="drop-hint">
                             결과 보고서를 등록하였습니다.
                        </p>
                        <button
                            type="button"
                            className="btn-primary"
                            disabled
                        >
                            파일선택 불가
                        </button>
                    </div>
                ) : (
                    // 보고서 없는 경우 → 업로드 가능
                    <>
                        <div
                            className={`dropzone ${isDragging ? "dragging" : ""}`}
                            onDragOver={onDragOver}
                            onDragEnter={onDragEnter}
                            onDragLeave={onDragLeave}
                            onDrop={onDrop}
                            role="button"
                            tabIndex={0}
                            onKeyDown={(e) =>
                                (e.key === "Enter" || e.key === " " ? onBrowseClick() : null)
                            }
                            aria-label="파일을 드래그하여 업로드하거나 클릭하여 선택"
                        >
                            <p className="drop-hint">
                                첨부할 파일을 여기로 끌어 놓거나, 파일 선택 버튼을 직접 선택해주세요.
                            </p>
                            <button type="button" className="btn-primary" onClick={onBrowseClick}>
                                파일선택
                            </button>
                            <input
                                ref={inputRef}
                                type="file"
                                accept={ACCEPT_EXT.join(",")}
                                className="sr-only"
                                onChange={onInputChange}
                            />
                        </div>

                        <ul className="report-list">
                            {reports.map((r) => (
                                <li key={r.id} className="report-row">
                                    <div className="report-info">
                                        결과 보고서 | {r.name} {r.ext} [{r.sizeLabel}]
                                    </div>
                                    <div className="report-actions">
                                        {r.status === "uploading" ? (
                                            <span className="upload-spinner" aria-label="업로드 중"/>
                                        ) : (
                                            <span className="upload-done-circle" title="업로드 완료"/>
                                        )}
                                        <button
                                            className="icon-btn"
                                            aria-label="삭제"
                                            onClick={() => removeOne(r.id)}
                                        >
                                            ✕
                                        </button>
                                    </div>
                                </li>
                            ))}
                        </ul>
                    </>
                )}
            </section>
        </Layout>
    );
}