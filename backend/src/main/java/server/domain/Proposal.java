package server.domain;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import lombok.Data;
import server.BackendApplication;

@Entity
@Table(name = "Proposal_table")
@Data
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // 고유 식별자

    private Integer totalEstimate;



    @Embedded
    private Photo content;

    @OneToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    // public static ProposalRepository repository() {
    //     ProposalRepository proposalRepository = BackendApplication.applicationContext.getBean(
    //         ProposalRepository.class
    //     );
    //     return proposalRepository;
    // }
}
//>>> DDD / Aggregate Root
