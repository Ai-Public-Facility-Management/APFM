package server.dto;

import lombok.Data;
import server.domain.FacilityStatus;
import server.domain.IssueType;
import server.domain.PublicFa;
import server.domain.PublicFaType;

@Data
public class PublicFaSummary {
    private Long publicFaId;
    private Long issueId;
    private String cameraName;
    private String publicFaType;
    private String condition;
    private FacilityStatus status;
    private boolean isProcessing;

    public PublicFaSummary(PublicFa fa) {
        this.publicFaId = fa.getId();
        this.cameraName = fa.getCamera().getLocation();
        this.publicFaType = fa.getType() != null ? fa.getType().getDisplayName() : null;
        if (fa.getIssue() != null) {
            this.issueId = fa.getIssue().getId();
            IssueType issueType = fa.getIssue().getType();
            this.condition = (issueType != null) ? issueType.getDisplayName() : null;
            this.isProcessing = fa.getIssue().isProcessing();
        }
        
        this.status = fa.getStatus();
    }

}
