package server.dto;

import lombok.Data;
import server.domain.FacilityStatus;
import server.domain.IssueType;
import server.domain.PublicFaType;

@Data
public class IssueSummary {
    private Long publicFaId;
    private PublicFaType publicFaType;
    private IssueType issueType;
    private Boolean isProcessing;
}
