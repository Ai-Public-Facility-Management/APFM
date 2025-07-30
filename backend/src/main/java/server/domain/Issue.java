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

    @OneToOne(mappedBy = "issue", cascade = CascadeType.ALL)
    private Proposal proposal;

    private Date creationDate;

    @Enumerated(EnumType.STRING)
    private IssueType type;

    @Embedded
    private Photo image;

    private String estimate;

    public static IssueRepository repository() {
        IssueRepository issueRepository = BackendApplication.applicationContext.getBean(
            IssueRepository.class
        );
        return issueRepository;
    }
}
//>>> DDD / Aggregate Root
