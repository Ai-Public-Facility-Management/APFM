package server.domain;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ResultReport")
@Data
public class ResultReport {
    //시공 완료 보고서
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private File file;

    private Date creationDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", unique = true)
    private Issue issue;

}
