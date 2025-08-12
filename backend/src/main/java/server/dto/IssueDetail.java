package server.dto;

import lombok.Data;
import server.domain.IssueType;
import server.domain.File;
import server.domain.PublicFaType;

@Data
public class IssueDetail {
    private PublicFaType publicFaType;
    private String cameraName;
    private IssueType condition;
    private Long obstruction;
    private File image;
    private Long estimate;
    private String estimateBasis;

}


