package server.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "InspectionSetting")
@AllArgsConstructor
@Builder
@Data
public class InspectionSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 시작 날짜 (ex: "2024-12-12")
    private String startDate;

    // 시작 시각 (ex: "09:00")
    private String startTime;

    // 점검 주기 (ex: 7일)
    private Integer inspectionCycle;

    // ✅ 마지막 점검 실행 시각 (nullable 가능)
    private LocalDateTime lastInspectedDate;

    public InspectionSetting() {
        this.startDate = LocalDate.now().toString();
        this.startTime = "09:00";
        this.inspectionCycle = 7;
    }

}
