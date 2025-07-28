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
@Table(name = "Camera_table")
@Data
//<<< DDD / Aggregate Root
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String location;

    private String ip;

    public static CameraRepository repository() {
        CameraRepository cameraRepository = BackendApplication.applicationContext.getBean(
            CameraRepository.class
        );
        return cameraRepository;
    }
}
//>>> DDD / Aggregate Root
