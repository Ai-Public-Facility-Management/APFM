//package server.dto;
//
//import lombok.Data;
//import java.util.List;
//
//@Data
//public class InspectionDetailResponseDTO {
//
//    private Long inspectionId;
//    private String createDate;             // ex) "2025.08.04 09:13:01"
//
//    private int issueCount;
//    private List<IssueDetailDTO> issues;
//
//    @Data
//    public static class IssueDetailDTO {
//        private String facilityName;       // 시설물 이름
//        private String cameraLocation;     // 카메라 위치, ex: "부산역 4번 출구 CCTV"
//        private String imageUrl;
//
//        private String issueType;          // ex: DAMAGE, MISSING 등
//        private String statusDescription;  // 상태 설명 (ex: 노후화)
//        private int obstructionLevel;      // 방해도 (숫자)
//        private String description;        // 관리자 메모/확인 요청 사항
//
//        private int estimatedRepairCost;   // 견적 (원 단위)
//        private String costBasis;          // 산출 근거 설명
//    }
//}
