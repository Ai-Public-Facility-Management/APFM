package server.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "Proposal_table")
@Data
public class Proposal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // 고유 식별자

    private Integer totalEstimate;

    // ✅ 제안서 파일 URL 및 설명
    private String fileUrl;
    private String fileDescription;

    @OneToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;
}
