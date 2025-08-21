package server.dto;

import lombok.Data;

@Data
public class ReportDTO {
    private String name;
    private String damage;
    private int estimated_cost;
    private String hindrance_level;
    private int complaints;
    private String last_repair_date;
    private String cost_basis;
    private float priority_score;
}
