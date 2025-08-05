package server.dto;

import lombok.Data;
import server.domain.FacilityStatus;
import server.domain.IssueType;
import server.domain.PublicFa;
import server.domain.PublicFaType;

@Data
public class PublicFaSummary {
    private Long publicFaId;
    private String cameraName;
    private PublicFaType publicFaType;
    private IssueType condition;
    private FacilityStatus status;
    private Boolean isProcessing;

    public PublicFaSummary(PublicFa fa){
        this.publicFaId = fa.getId();
        this.cameraName = fa.getCamera().getLocation();
        this.publicFaType = fa.getType();
        this.condition = fa.getIssue().getType();
        this.status = fa.getStatus();
        if(fa.getIssue().getProposal() != null)
            this.isProcessing = Boolean.TRUE;
        else
            this.isProcessing = Boolean.FALSE;

    }
}


