package server.dto;

import lombok.Data;

@Data
public class InspectionSettingRequestDTO {
    private String startDate;
    private String startTime;
    private Integer inspectionCycle;
    private String address;
}
