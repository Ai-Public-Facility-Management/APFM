package server.domain;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Table(name = "Report")
@Data
public class Report {

    @Id
    private Date creationDate;

    @Embedded
    private File content;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "inspection_id", unique = true)
    private Inspection inspection;

}