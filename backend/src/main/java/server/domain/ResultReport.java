package server.domain;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import server.BackendApplication;

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
