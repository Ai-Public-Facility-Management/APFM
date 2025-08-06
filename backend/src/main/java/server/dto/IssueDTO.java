package server.dto;

import jakarta.persistence.*;
import lombok.Data;
import server.domain.IssueType;
import server.domain.Photo;
import server.domain.Proposal;
import server.domain.PublicFa;

import java.util.Date;

@Data
public class IssueDTO {
    private Long id;

    private Long publicFaId;

    private Long resultId;

    private Long proposalId;

    private Date creationDate;

    private IssueType type;

    private Photo image;

    private Long estimate;

    private String estimateBasis;


}
