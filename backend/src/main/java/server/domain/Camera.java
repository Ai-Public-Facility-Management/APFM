package server.domain;


import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Entity
@Table(name = "Camera")
@Data
//<<< DDD / Aggregate Root
public class Camera {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String location;

    private String ip;

    @OneToMany(mappedBy = "camera",cascade = CascadeType.ALL)
    private List<PublicFa>  publicFa;


}
//>>> DDD / Aggregate Root
