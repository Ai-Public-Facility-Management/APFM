package server.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

import server.domain.IssueStatus;
import server.domain.ApprovalStatus;

@Data
public class InspectionResultDTO {

    private Detections detections;
    private String crops;
    private String original_image;

    @Data
    public static class Detections{
        private Long cameraId;
        private String publicFaType;              //시설물 종류
        private List<Integer> box;       // 점검 일자
        private String status;          // 이슈 종류
        private String cost_estimate;
    }
}
