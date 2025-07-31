package server.domain;

import java.util.Date;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import server.dto.PublicFaDTO;


@Entity
@Table(name = "PublicFa_table")
@NoArgsConstructor
@Data
//<<< DDD / Aggregate Root
public class PublicFa {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private PublicFaType type;

    private String section;

    private Date installDate;

    private Date lastRepair;

    private PublicFaStatus status;

    private Long obstruction;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "camera_id")
    private Camera camera;

    @OneToOne
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
}
//>>> DDD / Aggregate Root
