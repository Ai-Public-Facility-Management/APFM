package server.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.dto.InspectionResultDTO;


import java.util.Date;
import java.util.Objects;


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

    private String obstruction;

    private String obstruction_basis;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @OneToOne(mappedBy = "publicFa",cascade = CascadeType.ALL)
    private Issue issue;


    public PublicFa(PublicFaType type,Section section,FacilityStatus publicFaStatus,Camera camera,InspectionResultDTO.Detection detection) {
        this.type = type;
        this.section = section;
        this.camera = camera;
        this.status = publicFaStatus;
        this.installDate = new Date();
        this.lastRepair = new Date();
        this.obstruction = detection.getObstruction();
        this.obstruction_basis = detection.getObstructionBasis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PublicFa other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}

