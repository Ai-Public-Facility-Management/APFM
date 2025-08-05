package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.domain.Issue;
import server.domain.Proposal;
import server.repository.IssueRepository;
import server.repository.ProposalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final IssueRepository issueRepository;

    // ✅ 여러 이슈 ID에 대해 제안서를 생성하고 파일 URL 저장
    public void handleProposalGeneration(List<Long> issueIds, String fileUrl) {
        for (Long issueId : issueIds) {
            Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IllegalArgumentException("해당 이슈가 존재하지 않습니다."));

            if (issue.getProposal() != null) continue;

            Proposal proposal = new Proposal();
            proposal.setIssue(issue);
            proposal.setTotalEstimate(0);
            proposal.setFileUrl(fileUrl);
            proposal.setFileDescription("AI 기반 자동 생성 제안서");

            proposalRepository.save(proposal);
        }
    }
}
