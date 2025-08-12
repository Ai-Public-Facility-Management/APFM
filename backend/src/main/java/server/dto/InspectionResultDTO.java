package server.dto;

import lombok.Data;

import java.util.List;

@Data
public class InspectionResultDTO {

    private Long cameraId;
    private List<Detection> detections;
    private String crops;
    private String original_image;

    @Data
    public static class Detection {
        private String publicFaType;              //시설물 종류
        private List<Integer> box;       // 점검 일자
        private String status;          // 이슈 종류
        private String cost_estimate;
    }
}
