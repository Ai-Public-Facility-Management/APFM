package server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnmatchedFacilityDTO {
    private Long id;
    private String category;
    private String imageUrl;
    // private String location; // 필요시
}
