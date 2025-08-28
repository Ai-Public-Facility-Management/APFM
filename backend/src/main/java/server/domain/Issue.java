package server.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.dto.InspectionResultDTO;

import java.util.Date;
import java.util.Objects;


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

    @Column(length = 2000)
    private String estimateBasis;


    @Column(length = 1000)
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
        this.type = IssueType.fromDisplayName(detection.getIssueType());
        this.estimate = detection.getEstimate();
        this.estimateBasis = detection.getEstimate_basis();
        this.visionAnalysis = detection.getVisionAnalysis();
        this.creationDate = new Date();
        this.isProcessing = false;
    }

    public Issue update(PublicFa publicFa, InspectionResultDTO.Detection detection) {
        this.publicFa = publicFa;
        this.type = IssueType.fromDisplayName(detection.getIssueType());
        this.estimate = detection.getEstimate();
        this.estimateBasis = detection.getEstimate_basis();
        this.visionAnalysis = detection.getVisionAnalysis();
        this.creationDate = new Date();
        this.isProcessing = false;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublicFa other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}

