package untitled.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.persistence.*;
import lombok.Data;
import untitled.BackendApplication;

@Entity
@Table(name = "Inspection_table")
@Data
//<<< DDD / Aggregate Root
public class Inspection {

    @Id
    private Date createDate;

    private Boolean isinspected;

    public static InspectionRepository repository() {
        InspectionRepository inspectionRepository = BackendApplication.applicationContext.getBean(
            InspectionRepository.class
        );
        return inspectionRepository;
    }
}
//>>> DDD / Aggregate Root
