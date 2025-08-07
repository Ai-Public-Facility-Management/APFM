package server.domain;


import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "Camera")
@Data
//<<< DDD / Aggregate Root
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;

    private String ip;


}
//>>> DDD / Aggregate Root
