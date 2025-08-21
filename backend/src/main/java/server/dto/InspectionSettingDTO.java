package server.dto;

import lombok.Data;

@Data
public class InspectionSettingDTO {
    private String startDate;
    private String startTime;
    private Integer inspectionCycle;
}
