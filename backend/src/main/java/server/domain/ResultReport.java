package server.domain;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ResultReport")
@Data
public class ResultReport {

    @Id
    private Long id;

    @Embedded
    private File file;

    private Date creationDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", unique = true)
    private Issue issue;

}
