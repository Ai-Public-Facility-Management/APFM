package server.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

import server.domain.*;

@Data
public class DashboardInspection {

    private Long inspectionId;
    private Date inspectionDate;
    private String cameraName;
    private PublicFaType publicFaType;
    private IssueType issueType;

}

