import pedestrianVideo from '../assets/videos/pedestrian_demo3.mp4';
import pedestrianPoster from '../assets/videos/pedestrian_poster.jpg';

// src/content/serviceIntroData.ts
export type IntroStep = {
  id: string;
  title: string;
  desc: string;
  video?: string;
  poster?: string;
  images?: string[];
};

// CRA는 require.context 사용
// eslint-disable-next-line @typescript-eslint/no-var-requires
const ctx = (require as any).context('../assets/detect', false, /\.(png|jpe?g|webp)$/);
export const DETECT_IMAGES = ctx.keys().sort().map(ctx) as string[];

export const FEATURE_STEPS: IntroStep[] = [
  {
    id: "feat-detect",
    title: "시설물 자동 탐지",
    desc:
      "공공시설물 데이터로 사전 학습한 YOLO v11m 모델이 카메라 영상에서 볼라드, 보호펜스, 벤치 등 27종 시설물을 식별합니다. 사람이 일일이 확인하지 않아도 현황을 빠르게 파악할 수 있습니다.",
    images: DETECT_IMAGES,
  },
  {
    id: "feat-estimate",
    title: "AI 상태평가 및 견적 산출",
    desc:
      "한국건설기술연구원 ‘2025 건설공사 표준품셈’ 원문과 국토교통부 ‘2025 표준시장단가 DB’를 근거로, RAG 기반 LLM 랭그래프가 시설물 상태를 판정하고 필요한 공사 항목과 예상 비용을 산출합니다. 산출 근거가 함께 제공되어 검토가 용이합니다.",
  },
  {
    id: "feat-pedestrian",
    title: "보행 방해도 점수화",
    desc:
      "보행자 데이터로 학습한 Tracking 모드 YOLO v11m이 보행 흐름과의 간섭 정도를 분석해 ‘방해도 점수’를 제공합니다. 위험 요소나 통행 불편이 큰 지점을 우선 파악할 수 있습니다.",
    video:  pedestrianVideo, // 준비되면 경로 교체
    poster: pedestrianPoster,
  },
  {
    id: "feat-db",
    title: "시설물 DB 관리",
    desc:
      "시설물 종류, 상태, 방해도, 예상 견적 등 점검 결과를 시설물별 데이터베이스로 저장·관리합니다. 과거 이력과 공사 필요 여부를 한눈에 확인할 수 있어 의사결정이 빨라집니다.",
  },
  {
    id: "feat-report",
    title: "정기 점검 자동화 보고서 생성",
    desc:
      "모든 CCTV에 대해 설정된 주기대로 점검 내역을 자동 관리합니다. AI가 정기 점검 보고서와 철거·교체 우선순위 공문 초안을 생성하여 행정 문서 작업을 줄여줍니다.",
  },
  {
    id: "feat-proposal",
    title: "AI 공사 제안요청서 작성",
    desc:
      "공사가 필요한 시설물을 선택하면 AI가 공문 형식의 제안요청서 초안을 작성합니다. 담당자가 보완·수정 후 Word 파일로 내려받을 수 있습니다.",
  },
];

export const HOWTO_STEPS: IntroStep[] = [
  {
    id: "howto-cycle",
    title: "정기 점검 주기 설정",
    desc:
      "메인 페이지 상단 ‘점검주기 설정’에서 주기를 지정합니다. 설정된 주기마다 정기 점검 내역과 시설물 대시보드가 자동 갱신됩니다.",
  },
  {
    id: "howto-history",
    title: "정기점검 내역 조회 및 보고서 작성",
    desc:
      "‘정기점검 내역’에서 과거 기록을 확인하고, 상세 화면의 ‘정기 점검 보고서 작성’을 누르면 점검 보고서와 우선순위 보고서가 자동 작성됩니다.",
  },
  {
    id: "howto-dashboard",
    title: "공사 제안요청서 생성",
    desc:
      "‘시설물 리스트’에서 전체 DB를 조회합니다. 수리가 필요한 시설물을 다중 선택 후 ‘제안요청서 작성’을 누르면 초안이 생성되며, 검토·수정 후 Word 파일로 다운로드할 수 있습니다.",
  },
  {
    id: "howto-detail",
    title: "시설물 상세 조회와 결과보고서 업로드",
    desc:
      "시설물 상세 화면에서 이력, 견적, 상태를 확인합니다. 공사 완료 후 결과보고서를 업로드하면 마지막 공사 날짜가 자동 갱신됩니다.",
  },
  {
    id: "howto-board",
    title: "게시판과 AI 견적 활용",
    desc:
      "공지사항 확인과 게시글 작성이 가능합니다. 게시글 작성 시 이미지를 올리고 ‘AI 견적 생성’을 누르면 자동으로 산출된 견적 요약이 글 내용에 추가됩니다.",
  },
];
