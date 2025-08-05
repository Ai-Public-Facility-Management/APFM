package server.domain;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import server.BackendApplication;

@Entity
@Table(name = "ResultReport")
@Data
//<<< DDD / Aggregate Root
public class ResultReport {

    @Id
    private Integer proposalId;

    private Integer amount;

    private Date creationDate;

    // public static ResultReportRepository repository() {
    //     ResultReportRepository resultReportRepository = BackendApplication.applicationContext.getBean(
    //         ResultReportRepository.class
    //     );
    //     return resultReportRepository;
    // }
}
//>>> DDD / Aggregate Root
