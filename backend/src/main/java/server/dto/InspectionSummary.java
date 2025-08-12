package server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InspectionSummary {

    private Long id;
    private String createDate;        // ex) "2025.08.04 09:13:01"

    private String status;             // ✅ "작성중" / "작성 완료" 상태 문자열

    private int repairCount;          // 수리 필요 항목 수
    private int removalCount;         // 철거 필요 항목 수

    private boolean hasIssue;         // 이상 있음 여부 (issue 존재 여부)
}
