package server.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import server.domain.*;

import java.util.Date;

@Data
public class PublicFaDetail {
    private Long id;
    private String cameraName;
    private String type;
    private String image;
    private Section section;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date installDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date lastRepair;
    private String status;
    private String obstruction;
    private String obstruction_basis;
    private Long estimate;
    private String estimateBasis;
    private boolean hasReport;

    public PublicFaDetail(PublicFa publicFa, String url) {
        this.id = publicFa.getId();
        this.cameraName = publicFa.getCamera().getLocation();
        this.type = publicFa.getType() != null ? publicFa.getType().getDisplayName() : null;
        if (publicFa.getCamera() != null) {
            this.image = url;
        }
        if (publicFa.getIssue() != null) {
            this.estimate = publicFa.getIssue().getEstimate();
            this.estimateBasis = publicFa.getIssue().getEstimateBasis();
            this.hasReport = publicFa.getIssue().getResultReport() != null;
        }
        this.section = publicFa.getSection();
        this.installDate = publicFa.getInstallDate();
        this.lastRepair = publicFa.getLastRepair();
        this.status = publicFa.getStatus() != null ? publicFa.getStatus().getDisplayName() : null;
        this.obstruction = publicFa.getObstruction();
        this.obstruction_basis = publicFa.getObstruction_basis();
    }
}
