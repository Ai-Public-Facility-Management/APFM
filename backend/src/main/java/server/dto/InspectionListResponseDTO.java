package server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InspectionListResponseDTO {

    private Long id;
    private String createDate;        // ex) "2025.08.04 09:13:01"

    private boolean isInspected;      // 작성 여부 (true: 완료, false: 작성 중)

    private int repairCount;          // 수리 필요 항목 수
    private int removalCount;         // 철거 필요 항목 수

    private boolean hasIssue;         // 이상 있음 여부 (issue 존재 여부)
    private boolean hasReport;        // 보고서 존재 여부
}
