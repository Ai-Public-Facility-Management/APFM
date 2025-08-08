package server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import server.domain.Issue;
import server.domain.Proposal;
import server.domain.PublicFa;
import server.dto.IssueDTO;
import server.dto.IssueDetail;
import server.repository.InspectionRepository;
import server.repository.IssueRepository;
import server.repository.ProposalRepository;
import server.repository.PublicFaRepository;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private PublicFaRepository publicFaRepository;
    @Autowired
    private InspectionRepository inspectionRepository;

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
        return issueRepository.countByInspectionIdAndStatus(inspectionId,IssueStatus.REPAIR);
    }

    public int countRemovalIssues(Long inspectionId) {
        return issueRepository.countByInspectionIdAndStatus(inspectionId, IssueStatus.REMOVE);
    } // 점검별 remove 이슈

    public List<Issue> getIssuesByInspectionId(Long inspectionId) {
        return issueRepository.findByInspection_Id(inspectionId);
    }

    public Issue createIssue(IssueDTO issueDTO) {
        Issue issue = new Issue(issueDTO);
        issue.setPublicFa(publicFaRepository.findById(issueDTO.getPublicFaId()).orElse(new PublicFa()));

        return issueRepository.save(issue);
    }

    public Issue addIssue(String status,Long estimate,String estimateBasis,String image,PublicFa publicFa,Inspection inspection) {
        Issue issue = new Issue(IssueType.valueOf(status),estimate,estimateBasis,image);
        issue.setPublicFa(publicFa);
        issue.setInspection(inspection);
        return issueRepository.save(issue);
    }

    public void deleteIssue(IssueDTO issueDTO) {
        issueRepository.deleteById(issueDTO.getId());
    }

    public Proposal requestProposal(Long issue_id) {

        Issue issue= issueRepository.findById(issue_id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        /*
           파이썬에 이슈사항 내용을 바탕으로 제안서 생성 요청
        */
        return proposalRepository.save(new Proposal());
    }

    public List<Issue> getAllIssue(){
        return issueRepository.findAll();
    }

    public List<IssueDetail> getIssues(Long id){

        List<Issue> issues = issueRepository.findByInspection_IdOrderByIdDesc(id);
        List<IssueDetail> details = new ArrayList<>();
        issues.forEach(issue -> {
            IssueDetail detail = new IssueDetail();
            PublicFa publicFa = issue.getPublicFa();
            detail.setPublicFaType(publicFa.getType());
            detail.setCameraName(publicFa.getCamera().getLocation());
            detail.setCondition(issue.getType());
            detail.setObstruction(publicFa.getObstruction());
            detail.setImage(issue.getImage());
            detail.setEstimate(issue.getEstimate());
            detail.setEstimateBasis(issue.getEstimateBasis());

            details.add(detail);
        });

        return details;
    }

    public Issue updateIssue(IssueDTO issueDTO) {
        return issueRepository.findById(issueDTO.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

}

