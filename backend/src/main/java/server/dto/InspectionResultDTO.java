package server.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class InspectionResultDTO {

    private String email;              // 사용자 식별자
    private Date inspectionDate;       // 점검 일자
    private String reportUrl;          // 생성된 보고서 파일 URL
    private List<IssueDTO> issues;     // 탐지된 이슈 목록

    @Data
    public static class IssueDTO {
        private String location;       // 탐지된 시설물 위치
        private String imageUrl;       // 관련 이미지 URL
        private IssueStatus status;    // 예: REPAIR, REMOVE 등 
        private String content;        // 이슈 설명
        private Long cameraId;         // 촬영한 CCTV ID
        private Long facilityId;       // 감지된 시설물 ID
        private String category;       // 벤치, 펜스 등 분류
        private int obstructionLevel;  // 방해도
        private String description;    // 확인 요구 사항
    }
}
