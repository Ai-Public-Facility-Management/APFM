import React, { useState, useEffect } from "react";
import ReactMarkdown from "react-markdown";
import "./TermsModal.css";

interface TermsModalProps {
  onClose: () => void;
}

export default function TermsModal({ onClose }: TermsModalProps) {
  const [agreements, setAgreements] = useState({
    all: false,
    termsOfUse: false,
    privacyPolicy: false,
    thirdPartyConsent: false,
    identifierInfo: false,
  });

  const [activeClause, setActiveClause] = useState<string | null>(null);
  const [clauseContent, setClauseContent] = useState<string>("");
   const [loading, setLoading] = useState(false);

   // ✅ 약관 파일 불러오기
  useEffect(() => {
    if (activeClause) {
      setLoading(true);
      import(`../../assets/terms/${activeClause}.md`)
        .then((res) => fetch(res.default).then((r) => r.text()))
        .then((text) => {
          setClauseContent(text);
          setLoading(false);
        })
        .catch(() => {
          setClauseContent("약관 내용을 불러오는 데 실패했습니다.");
          setLoading(false);
        });
    }
  }, [activeClause]);

  const handleAllChange = () => {
    const newValue = !agreements.all;
    setAgreements({
      all: newValue,
      termsOfUse: newValue,
      privacyPolicy: newValue,
      thirdPartyConsent: newValue,
      identifierInfo: newValue,
    });
  };

  const handleChange = (field: keyof typeof agreements) => {
    setAgreements((prev) => {
      const updated = { ...prev, [field]: !prev[field] };
      const allChecked =
        updated.termsOfUse &&
        updated.privacyPolicy &&
        updated.thirdPartyConsent &&
        updated.identifierInfo;
      return { ...updated, all: allChecked };
    });
  };

  const openClause = (clause: string) => setActiveClause(clause);
  const closeClause = () => setActiveClause(null);

  const isAllRequiredAgreed =
    agreements.termsOfUse &&
    agreements.privacyPolicy &&
    agreements.thirdPartyConsent;

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
              [필수] APFM 이용약관 <button type="button" onClick={() => openClause("termsOfUse")}>약관읽기</button>
            </div>
            <p>APFM 이용약관에 대한 안내 사항을 읽고 동의합니다.</p>
            <label><input type="radio" name="termsOfUse" checked={!agreements.termsOfUse} onChange={() => handleChange("termsOfUse")} /> 동의안함</label>
            <label><input type="radio" name="termsOfUse" checked={agreements.termsOfUse} onChange={() => handleChange("termsOfUse")} /> 동의함</label>
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
              [필수] 개인정보 제3자 제공 동의 <button type="button" onClick={() => openClause("thirdPartyConsent")}>약관읽기</button>
            </div>
            <p>개인정보 제3자 제공 정책에 대한 동의서를 읽고 동의합니다.</p>
            <label><input type="radio" name="thirdPartyConsent" checked={!agreements.thirdPartyConsent} onChange={() => handleChange("thirdPartyConsent")} /> 동의안함</label>
            <label><input type="radio" name="thirdPartyConsent" checked={agreements.thirdPartyConsent} onChange={() => handleChange("thirdPartyConsent")} /> 동의함</label>
          </div>

          <div className="agreement-item">
            <div className="agreement-title">
              [선택] 고유식별정보 수집 및 이용 <button type="button" onClick={() => openClause("identifierInfo")}>약관읽기</button>
            </div>
            <p>고유식별정보 수집 및 이용에 대한 안내 사항을 읽고 이해했습니다.</p>
            <label>
              <input
                type="checkbox"
                name="identifierInfo"
                checked={agreements.identifierInfo}
                onChange={() => handleChange("identifierInfo")}
              />{" "}
              확인함
            </label>
          </div>
        </div>

        <div className="modal-buttons">
          <button onClick={() => isAllRequiredAgreed ? onClose() : alert("필수 항목에 모두 동의해주세요.")}>확인</button>
        </div>

        {activeClause && (
          <div className="clause-modal">
            <div className="clause-content">
              <h3>약관 상세 보기</h3>
              <div className="clause-scroll-box">
                {loading ? (
                  <div className="spinner" />
                ) : (
                  <ReactMarkdown>{clauseContent}</ReactMarkdown>
                )}
              </div>
              <button onClick={closeClause}>닫기</button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
