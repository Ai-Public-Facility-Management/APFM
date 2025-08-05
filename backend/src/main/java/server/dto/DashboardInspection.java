package server.dto;

import lombok.Data;
import server.domain.IssueType;
import server.domain.PublicFaType;

import java.util.Date;

@Data
public class DashboardInspection {

    private Long inspectionId;
    private Date inspectionDate;
    private String cameraName;
    private PublicFaType publicFaType;
    private IssueType issueType;

}

