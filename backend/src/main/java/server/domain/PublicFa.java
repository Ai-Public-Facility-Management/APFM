package server.domain;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "PublicFa_table")
@Data
//<<< DDD / Aggregate Root
public class PublicFa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long cameraId;

    private PublicFaType type;

    private String section;

    private Date installDate;

    private Date lastRepair;

    private PublicFaStatus status;

    private Long obstruction;

    @Column(name = "matched", nullable = false)
    private Boolean matched;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id")
    private Inspection inspection;

    private String category;

    private String imageUrl;

    @Column(name = "matched_public_fa_id")
    private Long matchedPublicFaId;

    
}
//>>> DDD / Aggregate Root
