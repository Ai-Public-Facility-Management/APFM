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
    //카메라 위치(ex) 5번지 4번 카메라)
    private String location;
    //ip주소
    private String ip;
    //위도, 경도
    private Float latitude;
    private Float longitude;
    @OneToMany(mappedBy = "camera",cascade = CascadeType.ALL)
    private List<PublicFa>  publicFa;


}
//>>> DDD / Aggregate Root
