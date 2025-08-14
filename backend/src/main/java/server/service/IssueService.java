package server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import server.domain.Issue;
import server.domain.PublicFa;
import server.dto.InspectionResultDTO;
import server.dto.IssueDTO;
import server.dto.IssueDetail;
import server.repository.IssueRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;


    public Issue addIssue(PublicFa publicFa,InspectionResultDTO.Detection detection) {
        Issue issue = new Issue(publicFa,detection);
        return issueRepository.save(issue);
    }

    public void deleteIssue(IssueDTO issueDTO) {issueRepository.deleteById(issueDTO.getId());}

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
            detail.setImage(issue.getPublicFa().getImage());
            detail.setEstimate(issue.getEstimate());
            detail.setEstimateBasis(issue.getEstimateBasis());

            details.add(detail);
        });

        return details;
    }

    public String setProcessing(List<Long> ids) {
        List<Issue> issues =  issueRepository.findAllById(ids);
        if(!issues.isEmpty()){
            issues.forEach(issue -> {
                issue.setProcessing(true);
            });
        }else
            return "fail";
        return "done";
    }

}

