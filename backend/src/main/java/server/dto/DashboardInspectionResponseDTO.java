package server.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class DashboardInspectionResponseDTO {
    private List<String> inspectionDate; // 날짜 리스트
    private Map<String, List<IssueDTO>> issues; // 날짜별 이슈 리스트

    @Data
    public static class IssueDTO {
        private Long id;
        private String content;
    }
}
