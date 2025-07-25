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
@Table(name = "Proposal_table")
@Data
//<<< DDD / Aggregate Root
public class Proposal {

    @Id
    private Integer totalEstimate;

    @Embedded
    private Photo content;

    public static ProposalRepository repository() {
        ProposalRepository proposalRepository = BackendApplication.applicationContext.getBean(
            ProposalRepository.class
        );
        return proposalRepository;
    }
}
//>>> DDD / Aggregate Root
