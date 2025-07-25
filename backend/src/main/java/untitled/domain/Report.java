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
@Table(name = "Report_table")
@Data
//<<< DDD / Aggregate Root
public class Report {

    @Id
    private Date creationDate;

    @Embedded
    private Photo content;

    public static ReportRepository repository() {
        ReportRepository reportRepository = BackendApplication.applicationContext.getBean(
            ReportRepository.class
        );
        return reportRepository;
    }
}
//>>> DDD / Aggregate Root
