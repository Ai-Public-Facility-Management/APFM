import { useEffect, useRef } from "react";
import { loadKakao } from "../../lib/loadKakao";
import "./main.css";
import Header from "../../components/Header";
import Footer from "../../components/Footer";

export default function MainPage() {
  const mapRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    let resizeHandler: (() => void) | null = null;

    async function init() {
      if (!mapRef.current) return;
      const kakao = await loadKakao();

      const center = new kakao.maps.LatLng(35.1379, 129.0556); // 예시: 부산
      const map = new kakao.maps.Map(mapRef.current, { center, level: 4 });

      // 컨트롤 (옵션)
      map.addControl(new kakao.maps.ZoomControl(), kakao.maps.ControlPosition.RIGHT);
      map.addControl(new kakao.maps.MapTypeControl(), kakao.maps.ControlPosition.TOPRIGHT);

      // 마커 하나
      new kakao.maps.Marker({ position: center, map });

      // 반응형
      resizeHandler = () => map.relayout();
      window.addEventListener("resize", resizeHandler);
    }

    init();
    return () => {
      if (resizeHandler) window.removeEventListener("resize", resizeHandler);
    };
  }, []);

  return (
    <div className="page-wrap">
      <Header />
      <main className="main-wrap">
        {/* …위쪽 테이블 영역들… */}
        <section className="map-wrap">
          <div ref={mapRef} className="map" />
        </section>
      </main>
      <Footer />
    </div>
  );
}
