package server.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class InspectionListResponseDTO {
    private Long id;
    private String inspectionDate;   // "2025.07.18 09:13:01"
    private String status;           // "작성중" or "작성 완료"
    private int repairCount;         // 수리 필요 항목 수
    private int removalCount;        // 철거 필요 항목 수
    private boolean hasIssue;        // 이상 없음 여부
    private boolean hasReport;       // 보고서 존재 여부
}
