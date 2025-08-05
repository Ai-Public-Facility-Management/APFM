package server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspection_cycle_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionCycleOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 주기 일수 (예: 7, 30, 90)
    private Integer cycleDay;
}