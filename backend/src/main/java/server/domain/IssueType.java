package server.domain;

public enum IssueType {
    DAMAGE("손상"),    // 손상
    MISSING("누락"),   // 누락
    OTHER("기타");    // 기타

    private final String displayName;

    IssueType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}