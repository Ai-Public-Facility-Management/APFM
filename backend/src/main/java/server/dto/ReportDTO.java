package server.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReportDTO {
    private String name;
    private String damage;
    private int estimated_cost;
    private String hindrance_level;
    private int complaints;
    private Date last_repair_date;
    private String cost_basis;
    private float priority_score;
}
