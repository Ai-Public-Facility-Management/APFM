package server.domain;

import java.util.Date;
import javax.persistence.*;
import lombok.Data;
import server.BackendApplication;

@Entity
@Table(name = "Report_table")
@Data
//<<< DDD / Aggregate Root
public class Report {

    @Id
    private Date creationDate;

    @Embedded
    private Photo content;

    // public static ReportRepository repository() {
    //     ReportRepository reportRepository = BackendApplication.applicationContext.getBean(
    //         ReportRepository.class
    //     );
    //     return reportRepository;
    // }
}
//>>> DDD / Aggregate Root
