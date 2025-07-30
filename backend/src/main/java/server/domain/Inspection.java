package server.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Inspection_table")
@Data
//<<< DDD / Aggregate Root
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Date createDate;

    private Boolean isinspected;

    // public static InspectionRepository repository() {
    //     return BackendApplication.applicationContext.getBean(InspectionRepository.class);
    // }
}
//>>> DDD / Aggregate Root
