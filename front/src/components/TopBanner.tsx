// src/components/TopBanner.tsx
import React from "react";
import koreaLogo2 from "../assets/korea2.png";
import "./Common.css"; // 공통 스타일

const TopBanner = () => (
  <div className="topBanner">
    <img src={koreaLogo2} alt="태극기" className="flag-icon" />
    <span>이 누리집은 대한민국 공식 전자정부 누리집입니다.</span>
  </div>
);

export default TopBanner;
