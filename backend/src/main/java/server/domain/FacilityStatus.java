package server.domain;

public enum FacilityStatus {
    NORMAL("정상"),             // 정상
    ABNORMAL("수리 필요");       // 비정상_이슈 발생

    private final String displayName;

    FacilityStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}