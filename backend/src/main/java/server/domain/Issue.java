package server.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.Date;

@Entity
@Table(name = "Issue_table")
@Data
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 어떤 점검에서 발생한 이슈인지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id")
    private Inspection inspection;

    // ✅ 제안서 연관 (1:1)
    @OneToOne(mappedBy = "issue", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Proposal proposal;

    // ✅ 이슈 유형: REPAIR, REMOVE, ...
    @Enumerated(EnumType.STRING)
    private IssueType type;

    // ✅ 이슈 이미지 (url + 설명)
    @Embedded
    private Photo image;

    // ✅ 이슈 설명 (UI에서 "확인 요청 사항" 또는 "내용" 등)
    private String description;

    // ✅ CCTV 위치명
    private String cameraName;

    // ✅ 처리 방법 (선택된 처리 유형)
    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    // ✅ 처리중 여부
    private boolean isProcessing;

    // 필요시 추가 가능:
    // private Date creationDate;
}
