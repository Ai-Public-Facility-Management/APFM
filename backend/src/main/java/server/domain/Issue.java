package server.domain;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;
import server.BackendApplication;


@Entity
@Table(name = "Issue_table")
@Data
//<<< DDD / Aggregate Root
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long publicFaId;

    private Long resultId;

    @OneToOne(mappedBy = "issue", cascade = CascadeType.ALL)
    private Proposal proposal;

    private Date creationDate;

    @Enumerated(EnumType.STRING)
    private IssueType type;

    @Embedded
    private Photo image;

    private String estimate;

    private Long inspectionId;

    private String cameraName;    // CCTV 위치명 (ex: 부산역 4번 cctv)

    private String description;   // 확인 요청 사항

    private String status;        // 상태 (정상 / 노후화 등)

    private int obstruction;      // 방해도

    private int repairCost;       // 수리 견적

    @Lob
    private String basis;         // 산출 근거 설명

}
//>>> DDD / Aggregate Root
