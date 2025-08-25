// src/components/HeroBanner/HeroBanner.tsx
import React from "react";
import "./HeroBanner.css";

type HeroBannerProps = {
  src: string;
  height?: string;          // "70vh" | "600px" 등
  fullBleed?: boolean;
  flushTop?: boolean;
  alt?: string;
  overlay?: boolean;

  /** 모양: none | soft(하단 모서리 둥글게) | arc(하단이 부드럽게 굴곡) */
  shape?: "none" | "soft" | "arc";
  /** 호버 인터랙션: zoom(살짝 확대) | none */
  hover?: "zoom" | "none";
  /** soft 모양일 때 둥글기(px) */
  radius?: number;
  /** arc 모양일 때 굴곡 높이 (px) */
  arcHeight?: number;
};

export default function HeroBanner({
  src,
  height = "70vh",
  fullBleed = true,
  flushTop = true,
  alt = "",
  overlay = false,
  shape = "soft",
  hover = "zoom",
  radius = 28,
  arcHeight = 56,
  children,
}: React.PropsWithChildren<HeroBannerProps>) {
  return (
    <section
      className={[
        "heroBanner",
        fullBleed ? "heroFullBleed" : "",
        flushTop ? "heroFlushTop" : "",
        shape === "soft" ? "heroSoft" : "",
        shape === "arc" ? "heroArc" : "",
        hover === "zoom" ? "heroHover" : "",
      ].join(" ")}
      style={
        {
          ["--hero-h" as any]: height,
          ["--hero-radius" as any]: `${radius}px`,
          ["--hero-arc-h" as any]: `${arcHeight}px`,
        } as React.CSSProperties
      }
      tabIndex={hover === "zoom" ? 0 : -1} // 키보드 포커스 시에도 동일 효과
    >
      <img className="heroImg" src={src} alt={alt} loading="eager" />
      {overlay && <div className="heroShade" aria-hidden />}
      {children ? <div className="heroInner">{children}</div> : null}
    </section>
  );
}
