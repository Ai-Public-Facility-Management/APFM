package server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalRequestDTO {
    private List<Long> ids;   // 이슈 ID 목록
    private String fileUrl;   // 제안서 파일 URL
}
