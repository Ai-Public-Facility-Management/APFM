import { useEffect, useRef, useState } from "react";
import { loadKakao } from "../../lib/loadKakao";
import "./main.css";
import Header from "../../components/Header";
import Footer from "../../components/Footer";
import {
  fetchDashboardInspections,
  fetchDashboardPublicFas,
  fetchAllCameras,
  InspectionDto,
  PublicFaDto,
} from "../../api/dashboard";
import { generateCCTVMarkerSvg } from "../../lib/markerSvg";
import { useNavigate } from "react-router-dom";
import em from "../../assets/emergency.png";

export default function MainPage() {
  const mapRef = useRef<HTMLDivElement | null>(null);
  const navigate = useNavigate();

  const [inspections, setInspections] = useState<InspectionDto[]>([]);
  const [publicFas, setPublicFas] = useState<PublicFaDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [err, setErr] = useState<string | null>(null);

  // 지도/마커
  const mapObjRef = useRef<any>(null);
  const markersRef = useRef<any[]>([]);
  const infoWindowsRef = useRef<any[]>([]);
  const [mapReady, setMapReady] = useState(false);

  // 카메라 목록 (API는 lat/lng만 주므로 표시용 이름 생성)
  type CameraUI = { id: number; name: string; latitude: number; longitude: number };
  const [cameras, setCameras] = useState<CameraUI[]>([]);
  const [selectedCamIdx, setSelectedCamIdx] = useState<number | null>(null);

  // 마커 색상 팔레트
  const colorList = ["#2573ff", "#28a745", "#fd7e14", "#dc3545", "#6f42c1", "#17a2b8"];

  // 지도 초기화
  useEffect(() => {
    let resizeHandler: (() => void) | null = null;
    async function init() {
      if (!mapRef.current) return;
      const kakao = await loadKakao();
      const center = new kakao.maps.LatLng(35.1379, 129.0556);
      const map = new kakao.maps.Map(mapRef.current, { center, level: 4 });
      map.addControl(new kakao.maps.ZoomControl(), kakao.maps.ControlPosition.RIGHT);
      map.addControl(new kakao.maps.MapTypeControl(), kakao.maps.ControlPosition.TOPRIGHT);

      mapObjRef.current = map;
      setMapReady(true);

      resizeHandler = () => map.relayout();
      window.addEventListener("resize", resizeHandler);
    }
    init();

    return () => {
      if (resizeHandler) window.removeEventListener("resize", resizeHandler);
      markersRef.current.forEach(m => m.setMap(null));
      markersRef.current = [];
      infoWindowsRef.current.forEach(iw => iw.close());
      infoWindowsRef.current = [];
      mapObjRef.current = null;
      setMapReady(false);
    };
  }, []);

  // 대시보드 데이터 조회
  useEffect(() => {
    let alive = true;
    (async () => {
      try {
        setLoading(true);
        const [i, p] = await Promise.all([
          fetchDashboardInspections(),
          fetchDashboardPublicFas(5),
        ]);
        if (!alive) return;
        setInspections(i ?? []);
        setPublicFas(p ?? []);
      } catch (e: any) {
        if (!alive) return;
        setErr(e?.message ?? "대시보드 로딩 실패");
      } finally {
        if (alive) setLoading(false);
      }
    })();
    return () => { alive = false; };
  }, []);

  // 카메라 목록 로드
  useEffect(() => {
    if (!mapReady) return;
    let alive = true;
    (async () => {
      try {
        const list = await fetchAllCameras(); // [{ latitude, longitude }]
        if (!alive) return;
        const withNames: CameraUI[] = (list ?? []).map((c, idx) => ({
          id: idx + 1,
          name: c.location ?? `CCTV-${idx + 1}`,
          latitude: Number(c.latitude),
          longitude: Number(c.longitude),
        }));
        setCameras(withNames);
      } catch (e: any) {
        if (!alive) return;
        console.error(e);
        setErr(prev => prev ?? "카메라 정보 로딩 실패");
      }
    })();
    return () => { alive = false; };
  }, [mapReady]);

  // 카메라 마커
  useEffect(() => {
    (async () => {
      const map = mapObjRef.current;
      if (!map) return;

      const kakao: any = await loadKakao();

      // 기존 마커/인포윈도우 제거
      markersRef.current.forEach(m => m.setMap(null));
      markersRef.current = [];
      infoWindowsRef.current.forEach(iw => iw.close());
      infoWindowsRef.current = [];

      if (cameras.length === 0) return;

      const bounds = new kakao.maps.LatLngBounds();

      cameras.forEach((cam, idx) => {
        if (!isFinite(cam.latitude) || !isFinite(cam.longitude)) return;

        const color = colorList[idx % colorList.length];
        const img = new kakao.maps.MarkerImage(
          generateCCTVMarkerSvg(color),
          new kakao.maps.Size(28, 28)
        );

        const pos = new kakao.maps.LatLng(cam.latitude, cam.longitude);
        const marker = new kakao.maps.Marker({ position: pos, image: img, map, title: cam.name });

        const content = `
          <div style="padding:8px 10px; max-width:220px;">
            <div style="font-weight:600; margin-bottom:4px;">${cam.name}</div>
            <div style="font-size:11px; color:#777;">
              lat: ${cam.latitude.toFixed(6)}, lng: ${cam.longitude.toFixed(6)}
            </div>
          </div>`;
        const iw = new kakao.maps.InfoWindow({ content });

        kakao.maps.event.addListener(marker, "click", () => {
          infoWindowsRef.current.forEach(i => i.close());
          iw.open(map, marker);
          setSelectedCamIdx(idx);
        });

        markersRef.current.push(marker);
        infoWindowsRef.current.push(iw);
        bounds.extend(pos);
      });

      if (!bounds.isEmpty()) map.setBounds(bounds);
    })();
  }, [cameras]);

  // CCTV 리스트 클릭 → 지도 이동 + 인포윈도우 열기
  const focusCamera = (idx: number) => {
    const cam = cameras[idx];
    const map = mapObjRef.current;
    const kakao = (window as any).kakao;
    if (!cam || !map || !kakao) return;
    const pos = new kakao.maps.LatLng(cam.latitude, cam.longitude);
    map.setCenter(pos);
    map.setLevel(4);
    infoWindowsRef.current.forEach(i => i.close());
    const marker = markersRef.current[idx];
    const iw = infoWindowsRef.current[idx];
    if (marker && iw) iw.open(map, marker);
    setSelectedCamIdx(idx);
  };

  // 유틸
  const date10 = (s?: string | null) => (s ? s.slice(0, 10) : "-");

  return (
    <div className="page-wrap">
      <Header />
      <main className="main-wrap">
        <div className="main-container">

          {/* === 상단 대시보드 === */}
          <section className="tables-wrap">

            {/* 정기점검 */}
            <div className="table-card">
              <div className="table-card__head">
                <h3>정기점검 내역</h3>
                {/* ✅ 변경: /inspection/list → /inspections */}
                <button className="icon-btn" onClick={() => navigate("/inspections")}>+</button>
              </div>
              <div className="table">
                {/* 헤더 행 */}
                <div className="table__row table__row--cols">
                  <div className="table__cell w-160">일시</div>
                  <div className="table__cell">상세</div>
                </div>

                {loading && <div className="table__row"><div className="table__cell">로딩중…</div></div>}
                {err && <div className="table__row"><div className="table__cell color-danger">{err}</div></div>}
                {!loading && !err && inspections.length === 0 && (
                  <div className="table__row"><div className="table__cell">데이터 없음</div></div>
                )}

                {inspections.map((it, i) => {
                  const hasDetail = it.cameraName || it.publicFaType || it.issueType;
                  const detailText = hasDetail
                    ? `${it.cameraName ?? ""} ${it.publicFaType ?? ""} ${it.issueType ?? ""}`.trim()
                    : "이상 없음";
                  return (
                    <div key={i} className="table__row">
                      <div className="table__cell w-160">{date10(it.inspectionDate)}</div>
                      <div
                        className="table__cell ellipsis link-like"
                        title={detailText}
                        // ✅ 변경: /inspection/{id} → /inspections/{id}
                        onClick={() => navigate(`/inspections/${it.inspectionId}`)}
                      >
                        {detailText}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* 시설물 */}
            <div className="table-card">
              <div className="table-card__head">
                <h3>시설물 대시보드</h3>
                <button className="icon-btn" title="전체보기" onClick={() => navigate("/facility-list")}>
                  +
                </button>
              </div>
              <div className="table">
                <div className="table__row table__row--cols">
                  <div className="table__cell w-120">종류</div>
                  <div className="table__cell w-220">카메라</div>
                  <div className="table__cell w-120">이슈</div>
                  <div className="table__cell w-120"></div>
                  <div className="table__cell" />
                </div>

                {loading && <div className="table__row"><div className="table__cell">로딩중…</div></div>}
                {err && <div className="table__row"><div className="table__cell color-danger">{err}</div></div>}
                {!loading && !err && publicFas.length === 0 && (
                  <div className="table__row"><div className="table__cell">데이터 없음</div></div>
                )}

                {publicFas.map((fa, i) => (
                  <div key={i} className="table__row">
                    <div
                      className="table__cell w-120 link-like"
                      onClick={() => navigate(`/detail/${fa.publicFaId}`)}
                      style={{ cursor: "pointer" }}
                      title="상세 보기"
                    >
                      {fa.publicFaType}
                    </div>
                    <div className="table__cell w-220 ellipsis">{fa.cameraName}</div>
                    <div className="table__cell w-120">{fa.issueType}</div>
                    <div className="table__cell w-120 flex-end">
                      {!fa.isProcessing ? (
                          <div className="badge_em">
                            <img src={em} alt="eme"/>
                          </div>
                      ) : (
                          <div className="badge">
                            <img src={em} alt="neme"/>
                            <span>수리중</span>
                          </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </section>

          {/* === 지도 + CCTV 사이드바 === */}
          <section className="map-wrap">
            <div className="map-grid">
              <div ref={mapRef} className="map" />
              <aside className="cctv-panel">
                <div className="cctv-panel__head">
                  <span role="img" aria-label="cctv">📷</span>&nbsp; CCTV 목록
                </div>
                <div className="cctv-panel__list">
                  {cameras.map((cam, idx) => (
                    <button
                      key={cam.id}
                      className={`cctv-item ${selectedCamIdx === idx ? "is-active" : ""}`}
                      onClick={() => focusCamera(idx)}
                      title={`${cam.name}`}
                    >
                      <span
                        className="cctv-dot"
                        style={{ backgroundColor: colorList[idx % colorList.length] }}
                      />
                      <div className="cctv-item__text">
                        <div className="cctv-item__name">{cam.name}</div>
                        <div className="cctv-item__coord">
                          ({cam.latitude.toFixed(4)}, {cam.longitude.toFixed(4)})
                        </div>
                      </div>
                    </button>
                  ))}
                  {cameras.length === 0 && (
                    <div className="cctv-empty">카메라 데이터 없음</div>
                  )}
                </div>
              </aside>
            </div>
          </section>

        </div>
      </main>
      <Footer />
    </div>
  );
}
