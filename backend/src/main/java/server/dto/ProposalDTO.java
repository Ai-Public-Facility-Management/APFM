package server.dto;

import lombok.Data;
import java.util.List;

/**
 * ✅ ProposalDTO (요청 + 응답용 통합 DTO)
 * - 요청: 프론트가 issue ID 리스트와 fileUrl을 보내고
 * - 응답: 백엔드는 같은 fileUrl을 그대로 다시 반환함
 */
@Data
public class ProposalDTO {

    // 📥 요청용: 여러 이슈 ID (프론트 → 백엔드)
    private List<Long> ids;

    // 📥 fileUrl은 프론트가 생성한 PDF 파일 경로
    // 📤 응답용: 백엔드는 저장 후 같은 URL을 그대로 응답으로 반환
    private String fileUrl;
}
