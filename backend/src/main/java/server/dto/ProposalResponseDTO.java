package server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProposalResponseDTO {
    private List<ProposalDTO> estimations; // 선택된 시설물들의 견적 정보
}
