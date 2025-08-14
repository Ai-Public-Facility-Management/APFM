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
    private PublicFaType publicFaType;
    private IssueType condition;
    private FacilityStatus status;

    public PublicFaSummary(PublicFa fa){
        this.publicFaId = fa.getId();
        this.cameraName = fa.getCamera().getLocation();
        this.publicFaType = fa.getType();
        if(fa.getIssue() != null){
            this.issueId = fa.getIssue().getId();
            this.condition = fa.getIssue().getType();
        }
        this.status = fa.getStatus();
    }
}


