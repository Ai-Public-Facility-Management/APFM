package server.dto;

import lombok.Data;
import java.util.List;

@Data
public class InspectionReportRequestDTO {
    private List<Long> issueIds;
}

@Data
public class InspectionReportResponseDTO {
    private String fileName;  // 프론트가 저장할 제안된 파일명
    private String content;   // 보고서 내용 (LLM 기반)
}

