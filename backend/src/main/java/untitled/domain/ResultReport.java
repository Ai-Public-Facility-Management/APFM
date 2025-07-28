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
@Table(name = "ResultReport_table")
@Data
//<<< DDD / Aggregate Root
public class ResultReport {

    @Id
    private Integer proposalId;

    private Integer amount;

    private Date creationDate;

    public static ResultReportRepository repository() {
        ResultReportRepository resultReportRepository = BackendApplication.applicationContext.getBean(
            ResultReportRepository.class
        );
        return resultReportRepository;
    }
}
//>>> DDD / Aggregate Root
