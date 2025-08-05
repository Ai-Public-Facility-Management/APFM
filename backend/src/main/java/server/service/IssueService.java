package server.service;

import io.lettuce.core.GeoArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import server.domain.Inspection;
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
    IssueRepository issueRepository;
    @Autowired
    PublicFaRepository publicFaRepository;
    @Autowired
    ProposalRepository proposalRepository;
    @Autowired
    InspectionRepository inspectionRepository;

    public Issue createIssue(IssueDTO issueDTO) {
        Issue issue = new Issue(issueDTO);
        issue.setPublicFa(publicFaRepository.findById(issueDTO.getPublicFaId()).orElse(new PublicFa()));

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
        Issue issue = issueRepository.findById(issueDTO.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        return issue;
    }
}
