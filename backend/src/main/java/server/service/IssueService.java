package server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.repository.*;
import server.dto.*;
import server.domain.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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
        // proposal.setContent("이슈 내용에 기반한 제안서입니다.");   -> 나중에 필요하면 생성 - 지금은 setTitle, setContent import 가 안돼서 방치
        proposal.setIssue(issue); // 연관 설정
        proposalRepository.save(proposal);

        issue.setProposal(proposal);
        issueRepository.save(issue);

        return proposal;
    }

    public int countRepairIssues(Long inspectionId) {
        return issueRepository.countByInspectionIdAndType(inspectionId, IssueType.REPAIR);
    } // 점검별 repair 이슈

    public int countRemovalIssues(Long inspectionId) {
        return issueRepository.countByInspectionIdAndType(inspectionId, IssueType.REMOVE);
    } // 점검별 remove 이슈

    public List<Issue> getIssuesByInspectionId(Long inspectionId) {
    return issueRepository.findByInspection_Id(inspectionId);
}
}
