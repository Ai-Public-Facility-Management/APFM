package server.domain;

import javax.persistence.*;
import lombok.Data;
import server.BackendApplication;

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

    // public static CameraRepository repository() {
    //     CameraRepository cameraRepository = BackendApplication.applicationContext.getBean(
    //         CameraRepository.class
    //     );
    //     return cameraRepository;
    // }
}
//>>> DDD / Aggregate Root
