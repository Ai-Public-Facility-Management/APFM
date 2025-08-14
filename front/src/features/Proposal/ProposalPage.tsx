import Layout from "../../components/Layout";
import React, { useEffect, useState } from "react";
import { Tabs, Tab, Typography } from "@mui/material";

import "./ProposalPage.css";
import {fetchProposal, ProposalData} from "../../api/publicFa";

const categoryMapping: Record<string, (keyof ProposalData)[]> = {
    "Ⅰ 공사 개요": ["project_overview", "construction_period"],
    "Ⅱ 현장 분석": ["site_analysis_summary"],
    "Ⅲ 세부 견적": ["estimation_details_with_basis", "total_cost"],
    "Ⅳ 계획": ["manpower_plan", "equipment_plan", "safety_quality_plan"],
    "Ⅴ 결론": ["conclusion_expected_effect"]
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

    if (!proposal) return <div>로딩 중...</div>;

    const categories = Object.keys(categoryMapping);
    const currentCategory = categories[value];
    const fields = categoryMapping[currentCategory];

    return (
        <Layout mainClassName="ProposalMain">
            <div className="proposal-container">
                <div className="proposal-header">
                    <h1>제안 요청서 수정</h1>
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
                                {fieldKey}
                            </Typography>
                            <textarea
                                className="proposal-textarea"
                                value={
                                    Array.isArray(proposal[fieldKey])
                                        ? (proposal[fieldKey] as string[]).join("\n")
                                        : (proposal[fieldKey] as string)
                                }
                                readOnly
                            />
                        </div>
                    ))}
                </div>
            </div>
        </Layout>
    );
};

export default ProposalPage;
