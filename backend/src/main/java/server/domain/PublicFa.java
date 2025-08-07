package server.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.dto.PublicFaDTO;

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

    //이미지상 좌표값
    @Embedded
    private Section section;


    private Date installDate;

    private Date lastRepair;

    //시설물 상태(정상-NORMAL,비정상-ABNORMAL)
    @Enumerated(EnumType.STRING)
    private FacilityStatus status;

    private Long obstruction;

    @ManyToOne(cascade = CascadeType.ALL,fetch=FetchType.LAZY)
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @OneToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "issue_id", unique = true)
    private Issue issue;


    public PublicFa updateFa(PublicFaDTO publicFaDTO) {
        this.setStatus(publicFaDTO.getStatus());
        this.setObstruction(publicFaDTO.getObstruction());
        this.setLastRepair(publicFaDTO.getLastRepair());
        return this;
    }

    public PublicFa(PublicFaDTO publicFaDTO) {
        this.setStatus(publicFaDTO.getStatus());
        this.setObstruction(publicFaDTO.getObstruction());
        this.setLastRepair(publicFaDTO.getLastRepair());
        this.setType(publicFaDTO.getType());
        this.setSection(publicFaDTO.getSection());
        this.setInstallDate(publicFaDTO.getInstallDate());
    }

    public PublicFa(PublicFaDTO publicFaDTO,Camera camera) {
        //수정 필요
        this.setStatus(FacilityStatus.NORMAL);
        this.setObstruction(publicFaDTO.getObstruction());
        this.setLastRepair(publicFaDTO.getLastRepair());
        this.setType(publicFaDTO.getType());
        this.setSection(publicFaDTO.getSection());
        this.setInstallDate(publicFaDTO.getInstallDate());
        this.setCamera(camera);
    }
}

