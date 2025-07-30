package server.domain;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import server.BackendApplication;

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

    public static InspectionRepository repository() {
        return BackendApplication.applicationContext.getBean(InspectionRepository.class);
    }
}
//>>> DDD / Aggregate Root
