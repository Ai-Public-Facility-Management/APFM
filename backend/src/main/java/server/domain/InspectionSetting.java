package server.domain;

import lombok.*;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "InspectionSetting")
@NoArgsConstructor
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

    // 지도 주소 (선택사항, 추후 사용)
    // private String address;

    // ✅ 마지막 점검 실행 시각 (nullable 가능)
    private LocalDateTime lastInspectedDate;

}
