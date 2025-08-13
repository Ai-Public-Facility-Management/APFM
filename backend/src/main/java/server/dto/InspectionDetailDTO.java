// 점검 상세 페이지용 DTO
package server.dto;

import lombok.Data;
import java.util.List;

@Data
public class InspectionDetailDTO {
    private Long id;
    private String createDate;
    private String status;
    private String facilityName;
    private String location;
    private String description;
    private String content;
    private List<String> imageUrlList;
    private List<IssueItem> issues;

    @Data
    public static class IssueItem {
        private Long id;
        private String facilityCategory;
        private String type;
        private String status;
        private Integer severity;
        private Integer level;
        private Integer count;
        private Long estimate;
        private String estimateBasis;
        private String description;
        private String content;
        private String imageUrl;
        private String aiImagePath;
    }
}
