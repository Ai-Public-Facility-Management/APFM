// server.dto.InspectionDetailResponseDTO.java

package server.dto;

import lombok.Data;
import java.util.Date;
import java.util.List;

@Data
public class InspectionDetailResponseDTO {
    private Long inspectionId;
    private Date createDate;
    private int issueCount;
    private List<IssueDetailDTO> issues;

    @Data
    public static class IssueDetailDTO {
        private String location;       // ex: 부산역 4번 cctv
        private String imageUrl;
        private String type;           // 문제 유형 (ex: 지하철 감지구)
        private String status;         // 정상 / 노후화 등
        private int obstruction;       // 방해도 (숫자)
        private String description;    // 확인 요청 사항
        private int repairCost;        // 견적 (단위: 원)
        private String basis;          // 산출 근거 설명
    }
}
