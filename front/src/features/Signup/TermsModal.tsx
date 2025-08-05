import React, { useState } from "react";
import "./TermsModal.css";

interface TermsModalProps {
  onClose: () => void;
}

export default function TermsModal({ onClose }: TermsModalProps) {
  const [agreements, setAgreements] = useState({
    all: false,
    personalInfo: false,
    privacyPolicy: false,
    onlinePolicy: false,
    educationInfo: false,
  });

  const [activeClause, setActiveClause] = useState<string | null>(null);

  const handleAllChange = () => {
    const newValue = !agreements.all;
    setAgreements({
      all: newValue,
      personalInfo: newValue,
      privacyPolicy: newValue,
      onlinePolicy: newValue,
      educationInfo: newValue,
    });
  };

  const handleChange = (field: keyof typeof agreements) => {
    setAgreements((prev) => {
      const updated = { ...prev, [field]: !prev[field] };
      const allChecked =
        updated.personalInfo &&
        updated.privacyPolicy &&
        updated.onlinePolicy &&
        updated.educationInfo;
      return { ...updated, all: allChecked };
    });
  };

  const openClause = (clause: string) => setActiveClause(clause);
  const closeClause = () => setActiveClause(null);

  const isAllRequiredAgreed =
    agreements.personalInfo &&
    agreements.privacyPolicy &&
    agreements.onlinePolicy;

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <h2>약관동의</h2>
        <div className="agreement-all">
          <div className="agree-header">
            <input
              type="checkbox"
              checked={agreements.all}
              onChange={handleAllChange}
              id="agreeAll"
            />
            <label htmlFor="agreeAll">
              APFM 서비스 약관에 모두 동의합니다. (전체 항목 동의)
            </label>
          </div>

          <div className="agree-description">
            <p>- 전체동의시 필수사항에 대해 일괄 동의하게 되며, 개별적으로 동의를 선택할 수 있습니다.</p>
            <p>- 필수 항목은 서비스 제공을 위해 필요한 항목이므로, 동의를 거부하시는 경우 서비스 이용에 제한이 있을 수 있습니다.</p>
          </div>
        </div>

        <div className="agreement-box">
          <div className="agreement-item">
            <div className="agreement-title">
              [필수] APFM 이용약관 <button type="button" onClick={() => openClause("personalInfo")}>약관읽기</button>
            </div>
            <p>APFM 이용약관에 대한 안내 사항을 읽고 동의합니다.</p>
            <label><input type="radio" name="personalInfo" checked={!agreements.personalInfo} onChange={() => handleChange("personalInfo")} /> 동의안함</label>
            <label><input type="radio" name="personalInfo" checked={agreements.personalInfo} onChange={() => handleChange("personalInfo")} /> 동의함</label>
          </div>

          <div className="agreement-item">
            <div className="agreement-title">
              [필수] 개인정보의 수집 및 이용 <button type="button" onClick={() => openClause("privacyPolicy")}>약관읽기</button>
            </div>
            <p>개인정보 수집 및 이용에 대한 약관을 읽고 동의합니다.</p>
            <label><input type="radio" name="privacyPolicy" checked={!agreements.privacyPolicy} onChange={() => handleChange("privacyPolicy")} /> 동의안함</label>
            <label><input type="radio" name="privacyPolicy" checked={agreements.privacyPolicy} onChange={() => handleChange("privacyPolicy")} /> 동의함</label>
          </div>

          <div className="agreement-item">
            <div className="agreement-title">
              [필수] 개인정보 제3자 제공 동의 <button type="button" onClick={() => openClause("onlinePolicy")}>약관읽기</button>
            </div>
            <p>개인정보 제3자 제공 정책에 대한 동의서를 읽고 동의합니다.</p>
            <label><input type="radio" name="onlinePolicy" checked={!agreements.onlinePolicy} onChange={() => handleChange("onlinePolicy")} /> 동의안함</label>
            <label><input type="radio" name="onlinePolicy" checked={agreements.onlinePolicy} onChange={() => handleChange("onlinePolicy")} /> 동의함</label>
          </div>

          <div className="agreement-item">
            <div className="agreement-title">
              [선택] 고유식별정보 수집 및 이용 <button type="button" onClick={() => openClause("educationInfo")}>약관읽기</button>
            </div>
            <p>고유식별정보 수집 및 이용에 대한 안내 사항을 읽고 이해했습니다.</p>
            <label>
              <input
                type="checkbox"
                checked={agreements.educationInfo}
                onChange={() => handleChange("educationInfo")}
              /> 확인함
            </label>
          </div>
        </div>

        <div className="modal-buttons">
          <button onClick={() => isAllRequiredAgreed ? onClose() : alert("필수 항목에 모두 동의해주세요.")}>확인</button>
        </div>

        {activeClause && (
          <div className="clause-modal">
            <div className="clause-content">
              <h3>약관 상세 보기: {activeClause}</h3>
              <p>여기에 {activeClause} 약관의 상세 내용이 표시됩니다. 실제로는 API 또는 파일에서 불러올 수 있습니다.</p>
              <button onClick={closeClause}>닫기</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
