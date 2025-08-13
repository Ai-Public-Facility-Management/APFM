package server.dto;

import lombok.Data;

import java.util.List;

@Data
public class InspectionResultDTO {

    private Long cameraId;                      //카메라 id
    private List<Detection> detections;         //시설물 및 이슈사항들
    private String original_image;              //전체 이미지 패스

    @Data
    public static class Detection {
        private String publicFaType;            //시설물 종류
        private List<Integer> box;              //이미지 박스
        private String issueType;               //이슈 종류
        private Long estimate;                   //견적 금액
        private String estimateBasis;           //견적 근거
        private Long obstruction;                //방해도
        private String obstructionBasis;        //방해도 근거
        private String visionAnalysis;          //이미지 설명
        private String crop_image;
    }
}
