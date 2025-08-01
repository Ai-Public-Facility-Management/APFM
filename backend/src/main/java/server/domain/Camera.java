package server.domain;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


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


}
//>>> DDD / Aggregate Root
