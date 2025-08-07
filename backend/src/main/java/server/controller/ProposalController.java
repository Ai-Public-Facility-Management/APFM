package server.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.repository.*;
import server.domain.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping("/api/proposals")  // ✅ prefix 명시 (가독성과 확장성)
@Transactional
public class ProposalController {

    @Autowired
    ProposalRepository proposalRepository;

    @Autowired
    IssueService issueService;

    // ✅ 이슈 ID를 기반으로 Proposal 자동 생성
    @PostMapping("/issues/{id}/generate")
    public Proposal generateProposal(@PathVariable Long id) {
        return issueService.generateProposalForIssue(id);
    }
}
