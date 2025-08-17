// src/components/ServiceIntro/ServiceIntro.tsx
import React, { useEffect, useRef, useState, useCallback } from "react";
import { motion, useReducedMotion } from "framer-motion";
import type { Transition } from "framer-motion";
import "./ServiceIntro.css";
import type { IntroStep } from "../../content/serviceIntroData";

const EASE: [number, number, number, number] = [0.22, 1, 0.36, 1];

export function ServiceIntro({
  featureSteps,
  howtoSteps,
  maxWidth = 1280,
  sectionGap = 48,
}: {
  featureSteps: IntroStep[];
  howtoSteps: IntroStep[];
  maxWidth?: number;
  sectionGap?: number;
}) {
  return (
    <>
      <CategoryIntro
        idName="features"
        title="주요 기능"
        steps={featureSteps}
        maxWidth={maxWidth}
        sectionGap={sectionGap}
      />
      <CategoryIntro
        idName="howto"
        title="이용 방법"
        steps={howtoSteps}
        maxWidth={maxWidth}
        sectionGap={sectionGap}
      />
    </>
  );
}

/* ───────────────── Category (네비 + 섹션) ───────────────── */

function CategoryIntro({
  idName,
  title,
  steps,
  maxWidth,
  sectionGap,
}: {
  idName: string;
  title: string;
  steps: IntroStep[];
  maxWidth: number;
  sectionGap: number;
}) {
  const reduceMotion = useReducedMotion();
  const tVideo: Transition = { duration: reduceMotion ? 0 : 0.45, ease: EASE };
  const tText: Transition  = { duration: reduceMotion ? 0 : 0.70, ease: EASE, delay: reduceMotion ? 0 : 0.06 };

  const panelRefs = useRef<(HTMLElement | null)[]>([]);
  const videoRefs = useRef<(HTMLVideoElement | null)[]>([]);
  const navWrapRef = useRef<HTMLDivElement | null>(null);

  const sectionTopsRef = useRef<number[]>([]);
  const [active, setActive] = useState(0);
  const [navHeight, setNavHeight] = useState(60);
  const clickLockRef = useRef<number>(0);

  // 네비 높이 측정
  useEffect(() => {
    const update = () => setNavHeight(navWrapRef.current?.offsetHeight ?? 60);
    update();
    window.addEventListener("resize", update);
    window.addEventListener("load", update);
    return () => {
      window.removeEventListener("resize", update);
      window.removeEventListener("load", update);
    };
  }, []);

  // 섹션 상단 좌표 캐시
  const recalcSectionTops = () => {
    sectionTopsRef.current = panelRefs.current.map((el) =>
      el ? el.getBoundingClientRect().top + window.scrollY : 0
    );
  };
  useEffect(() => {
    recalcSectionTops();
    window.addEventListener("resize", recalcSectionTops);
    window.addEventListener("load", recalcSectionTops);
    videoRefs.current.forEach((v) =>
      v?.addEventListener("loadedmetadata", recalcSectionTops)
    );
    return () => {
      window.removeEventListener("resize", recalcSectionTops);
      window.removeEventListener("load", recalcSectionTops);
      videoRefs.current.forEach((v) =>
        v?.removeEventListener("loadedmetadata", recalcSectionTops)
      );
    };
  }, [steps.length]);

  // 비디오 자동 재생/일시정지
  useEffect(() => {
    const ios: IntersectionObserver[] = [];
    videoRefs.current.forEach((el) => {
      if (!el) return;
      const io = new IntersectionObserver(
        (entries) => {
          const e = entries[0];
          if (!e) return;
          if (e.isIntersecting) el.play().catch(() => {});
          else el.pause();
        },
        { threshold: 0.35 }
      );
      io.observe(el);
      ios.push(io);
    });
    return () => ios.forEach((io) => io.disconnect());
  }, [steps.length]);

  // 스크롤 위치로 active 계산(카테고리 네비 바로 아래 기준선)
  useEffect(() => {
    let raf = 0;
    const onScroll = () => {
      cancelAnimationFrame(raf);
      raf = requestAnimationFrame(() => {
        if (Date.now() < clickLockRef.current) return;

        const stickyTop = parseInt(
          getComputedStyle(document.documentElement).getPropertyValue("--sticky-top") || "72",
          10
        ) || 72;
        const anchorY = window.scrollY + stickyTop + navHeight + 16;

        const tops = sectionTopsRef.current;
        if (!tops.length) return;

        let idx = 0;
        for (let i = 0; i < tops.length; i++) {
          if (anchorY >= tops[i] - 1) idx = i;
          else break;
        }
        setActive(idx);
      });
    };
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });
    window.addEventListener("resize", onScroll);
    return () => {
      cancelAnimationFrame(raf);
      window.removeEventListener("scroll", onScroll);
      window.removeEventListener("resize", onScroll);
    };
  }, [navHeight]);

  // 네비 클릭 → 정확 보정 스크롤
  const scrollToStep = (i: number) => {
    const target = panelRefs.current[i];
    if (!target) return;

    const stickyTop = parseInt(
      getComputedStyle(document.documentElement).getPropertyValue("--sticky-top") || "72",
      10
    ) || 72;
    const offset = stickyTop + navHeight + 16;
    const top = target.getBoundingClientRect().top + window.scrollY - offset;

    setActive(i);
    clickLockRef.current = Date.now() + 900;
    window.scrollTo({ top: Math.max(0, top), behavior: "smooth" });
  };

  const stickyTopVal = parseInt(
    getComputedStyle(document.documentElement).getPropertyValue("--sticky-top") || "72",
    10
  ) || 72;
  const scrollMarginPx = stickyTopVal + navHeight + 24;

  return (
    <section id={idName} className="si-wrap" style={{ maxWidth }}>
      <h2 className="si-title">{title}</h2>

      {/* 가로형 네비 */}
      <div ref={navWrapRef} className="si-stepNavWrap" aria-label={`${title} 네비게이션`}>
        <nav className="si-stepNav" role="tablist">
          {steps.map((s, i) => (
            <button
              key={s.id}
              role="tab"
              aria-selected={i === active}
              className={`si-pill ${i === active ? "isActive" : ""}`}
              onClick={() => scrollToStep(i)}
            >
              <span className="si-pillNum">{i + 1}</span>
              <span className="si-pillText">{s.title}</span>
            </button>
          ))}
        </nav>
      </div>

      {/* 섹션들 */}
      <div className="si-rows" style={{ rowGap: sectionGap }}>
        {steps.map((s, i) => (
          <section
            key={s.id}
            id={`${idName}-${s.id}`}
            ref={(el) => { panelRefs.current[i] = el; }}
            className="si-row"
            style={{ scrollMarginTop: scrollMarginPx }}
          >
            {/* 좌: 미디어(슬라이드 > 비디오 > 플레이스홀더) */}
            <motion.div
              className="si-media"
              initial={reduceMotion ? { opacity: 1, y: 0, scale: 1 } : { opacity: 0, y: 18, scale: 0.98 }}
              whileInView={{ opacity: 1, y: 0, scale: 1 }}
              viewport={{ amount: 0.55, once: false }}
              transition={tVideo}
            >
              {s.images?.length ? (
                <AutoSlideshow images={s.images} />
              ) : s.video ? (
                <video
                  ref={(el) => { videoRefs.current[i] = el; }}
                  className="si-video"
                  muted playsInline autoPlay loop controls preload="metadata"
                  poster={s.poster} src={s.video}
                  onLoadedMetadata={recalcSectionTops}
                />
              ) : (
                <div className="si-placeholder">
                  <div className="si-phThumb" />
                  <p className="si-phText">시연 영상 자리(임시)</p>
                </div>
              )}
            </motion.div>

            {/* 우: 텍스트(더 느리게) */}
            <motion.div
              className="si-text"
              initial={reduceMotion ? { opacity: 1, y: 0 } : { opacity: 0, y: 24 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ amount: 0.55, once: false }}
              transition={tText}
            >
              <div className="si-textHead">
                <span className="si-num">{i + 1}</span>
                <h3 className="si-textTitle">{s.title}</h3>
              </div>
              <p className="si-textDesc">{s.desc}</p>
            </motion.div>
          </section>
        ))}
      </div>
    </section>
  );
}

/* ───────────────── 슬라이드쇼 (슬라이드 전환, Hook 안전) ───────────────── */
function AutoSlideshow({
  images,
  interval = 1800,          // 다음 장으로 넘어가는 주기(ms)
  transitionMs = 520,        // 슬라이드 이동 시간(ms)
  pauseOnHover = true,
  direction = "left",        // "left" | "right"
}: {
  images: string[];
  interval?: number;
  transitionMs?: number;
  pauseOnHover?: boolean;
  direction?: "left" | "right";
}) {
  const reduceMotion = useReducedMotion();
  const imgs = images ?? [];

  // ✅ 모든 Hook은 최상단에서 선언
  const loadedRef = useRef<Record<string, boolean>>({});
  const [idx, setIdx] = useState(0);
  const [phase, setPhase] = useState<"idle" | "anim">("idle");
  const [nextIdx, setNextIdx] = useState(1);
  const timerRef = useRef<number | null>(null);

  // 프리로드(깜빡임 방지)
  useEffect(() => {
    imgs.forEach((src) => {
      const img = new Image();
      img.onload = () => { loadedRef.current[src] = true; };
      img.src = src;
    });
  }, [imgs]);

  const nextIndex = useCallback(
    (cur: number) =>
      direction === "left"
        ? (cur + 1) % imgs.length
        : (cur - 1 + imgs.length) % imgs.length,
    [direction, imgs.length]
  );

  const advance = useCallback(() => {
    if (phase === "anim" || imgs.length <= 1) return;
    const candidate = nextIndex(idx);
    if (!loadedRef.current[imgs[candidate]] && !reduceMotion) {
      window.setTimeout(advance, 120);
      return;
    }
    setNextIdx(candidate);
    setPhase("anim");
  }, [phase, imgs, nextIndex, idx, reduceMotion]);

  const start = useCallback(() => {
    if (timerRef.current != null || imgs.length <= 1) return;
    const dur = Math.max(800, reduceMotion ? 2600 : interval);
    timerRef.current = window.setInterval(advance, dur);
  }, [imgs.length, reduceMotion, interval, advance]);

  const stop = useCallback(() => {
    if (timerRef.current != null) {
      window.clearInterval(timerRef.current);
      timerRef.current = null;
    }
  }, []);

  // 타이머 시작/정리
  useEffect(() => { start(); return stop; }, [start, stop]);

  // ✅ 여기서부터 렌더 분기 (Hook 뒤)
  if (imgs.length === 0) return null;

  if (imgs.length === 1) {
    return (
      <div className="si-slideWrap">
        <img className="si-slideImg" src={imgs[0]} alt="" loading="eager" />
      </div>
    );
  }

  // 전환 설정
  const t = { duration: reduceMotion ? 0 : transitionMs / 1000, ease: [0.22, 1, 0.36, 1] as any };
  const enterX = direction === "left" ? "100%" : "-100%";
  const exitX  = direction === "left" ? "-100%" : "100%";

  return (
    <div
      className="si-slideWrap"
      onMouseEnter={pauseOnHover ? stop : undefined}
      onMouseLeave={pauseOnHover ? start : undefined}
      aria-label="자동 슬라이드 이미지"
    >
      <div className="si-slideStage">
        {/* 현재 이미지 */}
        <motion.img
          key={`curr-${idx}`}
          src={imgs[idx]}
          alt=""
          className="si-slideImg"
          initial={{ x: 0 }}
          animate={{ x: phase === "anim" ? exitX : 0 }}
          transition={t}
          style={{ zIndex: 1 }}
        />
        {/* 다음 이미지 */}
        <motion.img
          key={`next-${idx}-${nextIdx}`}
          src={imgs[nextIdx]}
          alt=""
          className="si-slideImg"
          initial={{ x: enterX }}
          animate={{ x: phase === "anim" ? 0 : enterX }}
          transition={t}
          style={{ zIndex: 2 }}
          onAnimationComplete={() => {
            if (phase === "anim") {
              setIdx(nextIdx);
              setPhase("idle");
            }
          }}
        />
      </div>
    </div>
  );
}


export default ServiceIntro;
