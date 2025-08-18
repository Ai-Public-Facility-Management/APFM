package server.dto;

import lombok.Data;
import java.util.List;

@Data
public class InspectionReportDTO {
    private List<Long> issueIds;
    private Long inspection_id;
}