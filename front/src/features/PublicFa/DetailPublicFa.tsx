import Layout from "../../components/Layout";
import "./DetailPublicFa.css";
import React, {useRef, useState} from "react";
import benchImg from "../../assets/detail_test_img.jpeg";
import {uploadResult} from "../../api/upload";

export default function DetailPublicFa(){
    const facilityDetailDummy = {
        sectionName: "B구역 2번 벤치",
        type: "벤치",
        cctvNumber: "16번",
        status: "파손",
        installDate: "2023-12-11",
        lastRepairDate: "2024-11-10",
        estimate: {
            description: [
                "공사 필요 구간 면적 약 10.85㎡",
                "필요 인력 건설기술(2인)",
                "공사 기간 약 1일",
                "필요 자재 나무 판재(480x80)",
                "약 200,000원",
            ],
        }
    };

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

    function addFiles(list: FileList | null) {
        if (!list) return;
        const arr = Array.from(list);
        const errs: string[] = [];
        const valid: File[] = [];

        arr.forEach(f => {
            const ext = pickExt(f.name);
            if (!ACCEPT_EXT.includes(ext)) errs.push(`${f.name}: 형식 미허용(${ext})`);
            else if (f.size > MAX_SIZE) errs.push(`${f.name}: 용량 초과(${humanSize(MAX_SIZE)} 이하)`);
            else valid.push(f);
        });

        const remain = Math.max(0, MAX_COUNT - reports.length);
        const trimmed = valid.slice(0, remain);
        if (valid.length > remain) errs.push(`최대 ${MAX_COUNT}개까지 업로드 가능합니다.`);

        if (errs.length) alert(errs.join("\n"));

        if (trimmed.length) {
            const newRows = trimmed.map(f => ({
                id: crypto.randomUUID(),
                file: f,
                name: f.name.replace(/\.[^/.]+$/, ""),
                ext: pickExt(f.name),
                sizeLabel: humanSize(f.size),
                status: "uploading" as const,
                progress: 0
            }));
            setReports(prev => [...prev, ...newRows]);

            // 업로드 시작
            newRows.forEach(row => {
                uploadResult(row.file, (percent) => {
                    setReports(prev =>
                        prev.map(r =>
                            r.id === row.id ? { ...r, progress: percent } : r
                        )
                    );
                }).then(() => {
                    setReports(prev =>
                        prev.map(r =>
                            r.id === row.id
                                ? { ...r, status: "done", progress: 100 }
                                : r
                        )
                    );
                }).catch(() => {
                    alert(`${row.name} 업로드 실패`);
                    setReports(prev => prev.filter(r => r.id !== row.id));
                });
            });
        }
    }

    const onDragOver  = (e: React.DragEvent) => { e.preventDefault(); setDragging(true); };
    const onDragEnter = (e: React.DragEvent) => { e.preventDefault(); setDragging(true); };
    const onDragLeave = () => setDragging(false);
    const onDrop      = (e: React.DragEvent) => { e.preventDefault(); setDragging(false); addFiles(e.dataTransfer.files); };

    const onBrowseClick = () => inputRef.current?.click();
    const onInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        addFiles(e.target.files);
        e.target.value = ""; // 같은 파일 재선택 가능하도록 초기화
    };

    const removeOne = (id: string) => {
        setReports(prev => prev.filter(r => r.id !== id));
    };


    return (
        <Layout mainClassName="detailMain">
            <h1>{facilityDetailDummy.sectionName}</h1>
            <hr className="custom-hr"/>
            <div className="detail-content">
                <table className="detail-table">
                    <tbody>
                    <tr>
                        <th>종류</th>
                        <td>{facilityDetailDummy.type}</td>
                    </tr>
                    <tr>
                        <th>CCTV</th>
                        <td>{facilityDetailDummy.cctvNumber}</td>
                    </tr>
                    <tr>
                        <th>상태</th>
                        <td>{facilityDetailDummy.status}</td>
                    </tr>
                    <tr>
                        <th>설치 날짜</th>
                        <td>{facilityDetailDummy.installDate}</td>
                    </tr>
                    <tr>
                        <th>마지막 수리 날짜</th>
                        <td>{facilityDetailDummy.lastRepairDate}</td>
                    </tr>
                    <tr>
                        <th>예상 견적</th>
                        <td>
                            {facilityDetailDummy.estimate.description.map((line, idx) => (
                                <div key={idx}>{line}</div>
                            ))}
                        </td>
                    </tr>
                    </tbody>
                </table>

                <div className="detail-image">
                    <img src={benchImg} alt={facilityDetailDummy.sectionName}/>
                </div>
            </div>
            <hr className="custom-hr-2"/>
            <section className="report-section">
                <h1 className="report-title">결과 보고서</h1>

                <div
                    className={`dropzone ${isDragging ? "dragging" : ""}`}
                    onDragOver={onDragOver}
                    onDragEnter={onDragEnter}
                    onDragLeave={onDragLeave}
                    onDrop={onDrop}
                    role="button"
                    tabIndex={0}
                    onKeyDown={(e) => (e.key === "Enter" || e.key === " " ? onBrowseClick() : null)}
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
                        multiple
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


            </section>
        </Layout>
    );
}