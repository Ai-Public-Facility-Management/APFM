package server.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import server.domain.InspectionSetting;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InspectionSettingDTO {
    private String startDate;
    private String startTime;
    private Integer inspectionCycle;
    // ✅ 엔티티 → DTO 변환 생성자
    public InspectionSettingDTO(InspectionSetting entity) {
        this.inspectionCycle = entity.getInspectionCycle();
        this.startTime = entity.getStartTime();
        this.startDate = entity.getStartDate();
    }
}
