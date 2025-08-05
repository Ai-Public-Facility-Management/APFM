package server.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Proposal")
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

}
