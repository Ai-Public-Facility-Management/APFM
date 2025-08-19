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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private Date creationDate;

    @Enumerated(EnumType.STRING)
    private IssueType type;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    private Long estimate;

    @Column(length = 500)
    private String estimateBasis;


    @Column(length = 500)
    private String visionAnalysis;

    private boolean isProcessing;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicFa_id", unique = true)
    private PublicFa publicFa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="inspection_id")
    private Inspection inspection;

    @OneToOne(mappedBy = "issue",cascade = CascadeType.ALL)
    private ResultReport resultReport;

    public Issue(PublicFa publicFa, InspectionResultDTO.Detection detection) {
        this.publicFa = publicFa;
        this.type = IssueType.valueOf(detection.getIssueType());
        this.estimate = detection.getEstimate();
        this.estimateBasis = detection.getEstimateBasis();
        this.visionAnalysis = detection.getVisionAnalysis();
        this.creationDate = new Date();
        this.isProcessing = false;
    }

}

