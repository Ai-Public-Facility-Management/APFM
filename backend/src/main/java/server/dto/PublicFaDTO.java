package server.dto;

import lombok.Data;
import server.domain.FacilityStatus;
import server.domain.PublicFaStatus;
import server.domain.PublicFaType;
import server.domain.Section;

import java.util.Date;

@Data
public class PublicFaDTO {
    private Long id;
    private Long cameraId;
    private PublicFaType type;
    private Section section;
    private Date installDate;
    private Date lastRepair;
    private FacilityStatus status;
    private Long obstruction;

    public PublicFaDTO(PublicFaType type, Section section, FacilityStatus status) {
        this.setType(type);
        this.setSection(section);
        this.setStatus(status);
        this.setInstallDate(null);
        this.setLastRepair(null);
        this.setObstruction(null);
    }
}
