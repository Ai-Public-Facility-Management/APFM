package server.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.dto.IssueDTO;

import java.util.Date;


@Entity
@Table(name = "Issue")
@NoArgsConstructor
@Data
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long resultId;

    @OneToOne(mappedBy = "issue", cascade = CascadeType.ALL)
    private Proposal proposal;

    private Date creationDate;

    @Enumerated(EnumType.STRING)
    private IssueType type;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;


    private Long estimate;

    @Column(length = 500)
    private String estimateBasis;

    private int obstructionLevel;

    private String description;

    private String location;

    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicFa_id", unique = true)
    private PublicFa publicFa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="inspection_id")
    private Inspection inspection;

//    public Issue(IssueDTO issueDTO) {
//        this.creationDate = issueDTO.getCreationDate();
//        this.type = issueDTO.getType();
//        this.image = issueDTO.getImage();
//        this.estimateBasis = issueDTO.getEstimateBasis();
//        this.estimate = issueDTO.getEstimate();
//    }

    public Issue(IssueType type,Long estimate,String estimateBasis) {
        this.creationDate = new Date();
        this.type = type;
        this.estimateBasis = estimateBasis;
        this.estimate = estimate;
    }

}

