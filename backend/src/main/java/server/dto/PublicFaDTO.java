package server.dto;

import lombok.Data;
import server.domain.PublicFaStatus;
import server.domain.PublicFaType;

import java.util.Date;

@Data
public class PublicFaDTO {
    private Long id;
    private Long cameraId;
    private PublicFaType type;
    private String section;
    private Date installDate;
    private Date lastRepair;
    private PublicFaStatus status;
    private Long obstruction;
}
