package server.domain;


import jakarta.persistence.*;
import lombok.Data;


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
