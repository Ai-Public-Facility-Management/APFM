package server.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.dto.InspectionResultDTO;


import java.util.Date;


@Entity
@Table(name = "PublicFa")
@NoArgsConstructor
@Data

public class PublicFa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //시설물 종류
    @Enumerated(EnumType.STRING)
    private PublicFaType type;

    @Embedded
    private File image;

    //이미지상 좌표값
    @Embedded
    private Section section;

    private Date installDate;

    private Date lastRepair;

    //시설물 상태(정상-NORMAL,비정상-ABNORMAL)
    @Enumerated(EnumType.STRING)
    private FacilityStatus status;

    private Long obstruction;

    private String obstruction_basis;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @OneToOne(mappedBy = "publicFa",cascade = CascadeType.ALL)
    private Issue issue;


    public PublicFa(PublicFaType type,Section section,FacilityStatus publicFaStatus,Camera camera,InspectionResultDTO.Detection detection) {
        File image = new File(detection.getCrop_image(),"image");
        this.type = type;
        this.section = section;
        this.camera = camera;
        this.status = publicFaStatus;
        this.image = image;
        this.installDate = new Date();
        this.lastRepair = null;
        this.obstruction = detection.getObstruction();
        this.obstruction_basis = detection.getObstructionBasis();
    }


}

