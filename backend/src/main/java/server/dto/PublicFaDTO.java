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
}
