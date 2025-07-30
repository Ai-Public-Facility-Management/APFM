package server.domain;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import server.BackendApplication;


@Entity
@Table(name = "Issue_table")
@Data
//<<< DDD / Aggregate Root
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long publicFaId;

    private Long resultId;

    private Long proposalId;

    private Date creationDate;

    @Enumerated(EnumType.STRING)
    private IssueType type;

    @Embedded
    private Photo image;

    private String estimate;

    
}
//>>> DDD / Aggregate Root
