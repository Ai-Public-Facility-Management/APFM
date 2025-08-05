package server.domain;

import java.util.Date;

import jakarta.persistence.*;
import lombok.Data;
import server.BackendApplication;

@Entity
@Table(name = "Report")
@Data
//<<< DDD / Aggregate Root
public class Report {

    @Id
    private Date creationDate;

    @Embedded
    private Photo content;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inspection_id", unique = true)
    private Inspection inspection;

}
//>>> DDD / Aggregate Root
