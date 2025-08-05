package server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspection_address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주소명 (예: "부산역 1번 출구 CCTV")
    private String locationName;
}