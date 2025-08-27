package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;
import server.service.ProposalService;

import java.util.List;

@RestController
@RequestMapping("/proposal")
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    /**
     * 선택된 ID들로 제안서 생성 (Spring → FastAPI 호출)
     */

    @PostMapping("/generate")
    public ResponseEntity<String> generateProposal(@RequestBody List<Long> ids) {
        return proposalService.generateProposalForIds(ids);

    }

}
