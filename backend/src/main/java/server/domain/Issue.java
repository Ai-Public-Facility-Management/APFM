package server.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.dto.InspectionResultDTO;

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


    private Date creationDate;

    @Enumerated(EnumType.STRING)
    private IssueType type;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    private Long estimate;

    @Column(length = 500)
    private String estimateBasis;

    @Column(length = 500)
    private String estimateReferences;

    private Long obstruction;

    @Column(length = 500)
    private String obstructionBasis;

    @Column(length = 500)
    private String visionAnalysis;

    private boolean isProcessing;

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

    public Issue(PublicFa publicFa, InspectionResultDTO.Detection detection) {
        this.publicFa = publicFa;
        this.type = IssueType.valueOf(detection.getIssueType());
        this.estimate = detection.getEstimate();
        this.estimateBasis = detection.getEstimateBasis();
        this.obstruction = detection.getObstruction();
        this.obstructionBasis = detection.getObstructionBasis();
        this.visionAnalysis = detection.getVisionAnalysis();
        this.creationDate = new Date();
        this.isProcessing = false;
    }

}

