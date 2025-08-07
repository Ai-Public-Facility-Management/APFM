package server.domain;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.dto.IssueDTO;


@Entity
@Table(name = "Issue")
@NoArgsConstructor
@Data
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long resultId;

    @OneToOne(mappedBy = "issue", cascade = CascadeType.ALL)
    private Proposal proposal;

    private Date creationDate;

    @Enumerated(EnumType.STRING)
    private IssueType type;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @Embedded
    private Photo image;

    private Long estimate;

    @Column(length = 500)
    private String estimateBasis;

    @OneToOne(mappedBy = "issue",cascade = CascadeType.ALL)
    private PublicFa publicFa;

    @ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "inspection_id")
    private Inspection inspection;

    public Issue(IssueDTO issueDTO) {
        this.creationDate = issueDTO.getCreationDate();
        this.type = issueDTO.getType();
        this.image = issueDTO.getImage();
        this.estimateBasis = issueDTO.getEstimateBasis();
        this.estimate = issueDTO.getEstimate();
    }

    private String content;
    
}

