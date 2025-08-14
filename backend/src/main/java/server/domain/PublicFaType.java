package server.domain;

public enum PublicFaType {
    STREET_LIGHT("가로등"),
    CCTV("CCTV"),
    BENCH("벤치"),
    TRASH_CAN("쓰레기통"),
    BOLLARD("볼라드");  //일단 예시로 넣고 나중에 yolo 학습 내용으로 교체

    private final String displayName;

    PublicFaType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
