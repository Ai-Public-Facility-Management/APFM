// 점검 상세 페이지용 DTO
package server.dto;

import lombok.Data;
import java.util.List;

@Data
public class InspectionDetailDTO {
    private Long id;
    private String createDate;              //정기점검 날짜
    private List<Camera> cameras;         //이슈사항
    private String status;                  //보고서 작성 여부
    private String report_path;             //보고서

    @Data
    public static class Camera{
        private String cameraName;              //카메라 이름(위치)
        private String imageUrl;                //카메라 캡처 이미지
        private List<IssueItem> issues;
    }
    @Data
    public static class IssueItem {
        private Long id;                    //이슈사항 Id
        private String publicFaType;        //시설물 종류
        private String type;                //이슈사항 타입
        private Long estimate;              //견적
        private String estimateBasis;       //견적 근거
        private String obstruction;         //방해도

    }
}
