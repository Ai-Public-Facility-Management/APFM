package server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspection_setting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
