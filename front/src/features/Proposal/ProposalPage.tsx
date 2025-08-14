import Layout from "../../components/Layout";
import React, { useEffect, useState } from "react";
import { Tabs, Tab, Typography, Button } from "@mui/material";

import "./ProposalPage.css";
import { fetchProposal, ProposalData, saveProposal } from "../../api/publicFa";

const categoryMapping: Record<string, (keyof ProposalData)[]> = {
    "Ⅰ 공사 개요": ["project_overview", "construction_period"],
    "Ⅱ 현장 분석": ["site_analysis_summary"],
    "Ⅲ 세부 견적": ["estimation_details_with_basis", "total_cost"],
    "Ⅳ 계획": ["manpower_plan", "equipment_plan", "safety_quality_plan"],
    "Ⅴ 결론": ["conclusion_expected_effect"]
};

const subTitleMapping: Record<string, string> = {
    project_overview: "사업 개요",
    construction_period: "공사 소요 기간",
    site_analysis_summary: "현장 분석 요약",
    estimation_details_with_basis: "세부 견적 및 계산 근거 요약",
    total_cost: "총 금액",
    manpower_plan: "작업 인력 계획",
    equipment_plan: "장비 계획",
    safety_quality_plan: "안전 및 품질관리 계획",
    conclusion_expected_effect: "결론 및 기대효과"
};

const ProposalPage = () => {
    const [value, setValue] = useState(0);
    const [proposal, setProposal] = useState<ProposalData | null>(null);

    useEffect(() => {
        fetchProposal().then(setProposal).catch(console.error);
    }, []);

    const handleChange = (event: React.SyntheticEvent, newValue: number) => {
        setValue(newValue);
    };

    const handleSave = async () => {
        if (!proposal) return;
        try {
            await saveProposal(proposal); // FastAPI로 전송 + DOCX 다운로드
            alert("제안서가 다운로드되었습니다.");
        } catch (err) {
            console.error(err);
            alert("저장 실패");
        }
    };

    if (!proposal) return <div>로딩 중...</div>;

    const categories = Object.keys(categoryMapping);
    const currentCategory = categories[value];
    const fields = categoryMapping[currentCategory];

    return (
        <Layout mainClassName="ProposalMain">
            <div className="proposal-container">
                <div className="proposal-header">
                    <h1>제안 요청서 수정</h1>
                    <Button
                        variant="contained"
                        color="primary"
                        onClick={handleSave}

                    >
                        저장하기
                    </Button>
                </div>

                {/* 탭 버튼 */}
                <Tabs value={value} onChange={handleChange}>
                    {categories.map((cat, idx) => (
                        <Tab key={idx} label={cat} />
                    ))}
                </Tabs>

                {/* 탭 내용 */}
                <div className="proposal-box">
                    <Typography className="proposal-category-title">
                        {currentCategory}
                    </Typography>

                    {fields.map((fieldKey) => (
                        <div key={fieldKey} className="proposal-field">
                            <Typography className="proposal-field-label">
                                {subTitleMapping[fieldKey] || fieldKey}
                            </Typography>
                            <textarea
                                className="proposal-textarea"
                                value={
                                    Array.isArray(proposal[fieldKey])
                                        ? (proposal[fieldKey] as string[]).join("\n")
                                        : (proposal[fieldKey] as string)
                                }

                                onChange={(e) => {
                                    const newValue = e.target.value;
                                    const textarea = e.target;
                                    textarea.style.height = "auto"; // 높이 초기화
                                    textarea.style.height = `${textarea.scrollHeight}px`; // 내용에 맞춰 높이 조정
                                    setProposal((prev) => {
                                        if (!prev) return prev;
                                        return {
                                            ...prev,
                                            [fieldKey]: Array.isArray(prev[fieldKey])
                                                ? newValue.split("\n")
                                                : newValue
                                        };
                                    });
                                }}
                            />
                        </div>
                    ))}
                </div>
            </div>
        </Layout>
    );
};

export default ProposalPage;
