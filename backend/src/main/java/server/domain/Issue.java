package server.domain;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.dto.IssueDTO;


@Entity
@Table(name = "Issue_table")
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

    @Embedded
    private Photo image;

    private String estimate;

    @OneToOne(mappedBy = "issue",cascade = CascadeType.ALL)
    private PublicFa publicFa;


    public Issue(IssueDTO issueDTO) {
        this.creationDate = issueDTO.getCreationDate();
        this.type = issueDTO.getType();
        this.image = issueDTO.getImage();
        this.estimate = issueDTO.getEstimate();
    }
    
}

