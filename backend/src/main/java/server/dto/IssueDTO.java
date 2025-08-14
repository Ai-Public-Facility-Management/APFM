package server.dto;

import lombok.Data;
import server.domain.File;
import server.domain.IssueType;

import java.util.Date;

@Data

public class IssueDTO {
    private Long id;

    private Long publicFaId;

    private Long resultId;

    private Long proposalId;

    private Date creationDate;

    private IssueType type;

    private File image;

    private Long estimate;

    private String estimateBasis;


}
