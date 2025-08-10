let kakaoLoading: Promise<typeof window.kakao> | null = null;

export function loadKakao(): Promise<typeof window.kakao> {
  // @ts-ignore
  if (window.kakao && window.kakao.maps) return Promise.resolve(window.kakao);

  if (!kakaoLoading) {
    kakaoLoading = new Promise((resolve, reject) => {
      const key = process.env.REACT_APP_KAKAO_JS_KEY as string;
      if (!key) return reject(new Error("REACT_APP_KAKAO_JS_KEY가 없습니다."));
      const url = `//dapi.kakao.com/v2/maps/sdk.js?appkey=${key}&autoload=false&libraries=services,clusterer,drawing`;
      const s = document.createElement("script");
      s.src = url;
      s.async = true;
      s.onload = () => {
        // @ts-ignore
        window.kakao.maps.load(() => resolve(window.kakao));
      };
      s.onerror = reject;
      document.head.appendChild(s);
    });
  }
  return kakaoLoading!;
}
