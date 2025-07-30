package server.domain;

import javax.persistence.*;
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

    // private String title;

    // private String content;

    @Embedded
    private Photo content;

    @OneToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;

    public static ProposalRepository repository() {
        ProposalRepository proposalRepository = BackendApplication.applicationContext.getBean(
            ProposalRepository.class
        );
        return proposalRepository;
    }
}
