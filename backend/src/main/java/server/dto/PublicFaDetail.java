package server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import server.domain.*;

import java.util.Date;

@Data
public class PublicFaDetail {
    private Long id;
    private String cameraName;
    private PublicFaType type;
    private String image;
    private Section section;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul")
    private Date installDate;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="Asia/Seoul")
    private Date lastRepair;
    private FacilityStatus status;
    private Long obstruction;
    private Long estimate;
    private String estimateBasis;

    public PublicFaDetail(PublicFa publicFa) {
        this.id = publicFa.getId();
        this.cameraName = publicFa.getCamera().getLocation();
        this.type = publicFa.getType();
        if (publicFa.getIssue() != null) {
            this.image = publicFa.getImage().getUrl();
            this.estimate = publicFa.getIssue().getEstimate();
            this.estimateBasis = publicFa.getIssue().getEstimateBasis();
        }
        this.section = publicFa.getSection();
        this.installDate = publicFa.getInstallDate();
        this.lastRepair = publicFa.getLastRepair();
        this.status = publicFa.getStatus();
        this.obstruction = publicFa.getObstruction();
    }
}
