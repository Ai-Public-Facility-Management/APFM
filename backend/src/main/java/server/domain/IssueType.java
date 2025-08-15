package server.domain;

public enum IssueType {// 손상
    NORMAL("정상"),
    PEELING("표면 벗겨짐"),
    DAMAGE("파손"),
    DEFORMATION("변형"),
    DISCOLORATION("변색"),
    CRACK("균열");

    private final String displayName;

    IssueType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}