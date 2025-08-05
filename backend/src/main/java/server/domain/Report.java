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
    private Photo content;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "inspection_id", unique = true)
    private Inspection inspection;

}