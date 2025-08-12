// src/lib/markerSvg.ts

/** 색상별 CCTV 마커 SVG(Data URL) 생성 */
export function generateCCTVMarkerSvg(color: string): string {
  const svg = `
    <svg width="28" height="28" xmlns="http://www.w3.org/2000/svg">
      <circle cx="14" cy="14" r="12" fill="${color}" stroke="black" stroke-width="2"/>
      <text x="14" y="18" text-anchor="middle" font-size="12" fill="white" font-weight="bold">C</text>
    </svg>
  `;
  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
}
