package server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inspection_time_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspectionTimeOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 시각 (예: "09:00")
    private String time;
}