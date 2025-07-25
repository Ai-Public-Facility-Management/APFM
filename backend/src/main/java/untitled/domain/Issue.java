package untitled.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import untitled.BackendApplication;

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

    private type type;

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
