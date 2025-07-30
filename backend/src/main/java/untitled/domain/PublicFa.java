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
import untitled.domain.PublicFaStatus;
import untitled.domain.PublicFaType;


@Entity
@Table(name = "PublicFa_table")
@Data
//<<< DDD / Aggregate Root
public class PublicFa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long cameraId;

    private PublicFaType type;

    private String section;

    private Date installDate;

    private Date lastRepair;

    private PublicFaStatus status;

    private Long obstruction;

    public static PublicFaRepository repository() {
        PublicFaRepository publicFaRepository = BackendApplication.applicationContext.getBean(
            PublicFaRepository.class
        );
        return publicFaRepository;
    }
}
//>>> DDD / Aggregate Root
