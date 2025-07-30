package server.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    public Proposal generateProposalForIssue(Long issueId) {
        Issue issue = issueRepository.findById(issueId)
            .orElseThrow(() -> new IllegalArgumentException("해당 이슈가 존재하지 않습니다."));

        // 이미 제안서가 존재하면 예외 처리
        if (issue.getProposal() != null) {
            throw new IllegalStateException("이미 제안서가 생성된 이슈입니다.");
        }

        Proposal proposal = new Proposal();
        // proposal.setTitle("자동 생성 제안서 - Issue #" + issueId);
        // proposal.setContent("이슈 내용에 기반한 제안서입니다.");
        proposal.setIssue(issue); // 연관 설정

        proposalRepository.save(proposal);

        issue.setProposal(proposal);
        issueRepository.save(issue);

        return proposal;
    }
}
