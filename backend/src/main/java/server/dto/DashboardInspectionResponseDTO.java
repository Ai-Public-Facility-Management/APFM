package server.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

import server.domain.*;

@Data
public class DashboardInspectionResponseDTO {

    private List<String> inspectionDate;
    private Map<String, List<IssueSummaryDTO>> issues;

    @Data
    public static class IssueSummaryDTO {
        private IssueStatus status;  // REPAIR, REMOVE
        private int count;           // 건수
    }
}

