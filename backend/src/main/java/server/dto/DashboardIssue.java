package server.dto;

import lombok.Data;
import server.domain.IssueType;
import server.domain.PublicFaType;

@Data
public class DashboardIssue {
    private Long publicFaId;
    private PublicFaType publicFaType;
    private IssueType issueType;
    private String cameraName;
    private Boolean isProcessing;
}
