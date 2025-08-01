package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import server.domain.Issue;
import server.domain.Proposal;
import server.domain.PublicFa;
import server.dto.IssueDTO;
import server.repository.IssueRepository;
import server.repository.ProposalRepository;
import server.repository.PublicFaRepository;

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

    public Issue getIssue(Long id){
        return issueRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Issue updateIssue(IssueDTO issueDTO) {
        Issue issue = issueRepository.findById(issueDTO.getId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        return issue;
    }
}
