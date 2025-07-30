package server.domain;


import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import server.BackendApplication;

@Entity
@Table(name = "Proposal_table")
@Data
//<<< DDD / Aggregate Root
public class Proposal {

    @Id
    private Integer totalEstimate;

    @Embedded
    private Photo content;

    // public static ProposalRepository repository() {
    //     ProposalRepository proposalRepository = BackendApplication.applicationContext.getBean(
    //         ProposalRepository.class
    //     );
    //     return proposalRepository;
    // }
}
//>>> DDD / Aggregate Root
