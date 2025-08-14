package server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import server.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import server.domain.Issue;
import server.domain.PublicFa;
import server.dto.InspectionResultDTO;
import server.dto.IssueDTO;
import server.dto.IssueDetail;

import server.repository.*;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private PublicFaRepository publicFaRepository;
    @Autowired
    private InspectionRepository inspectionRepository;
    @Autowired
    private AzureService azureService;
    @Autowired
    private ResultReportRepository resultRepository;

    public int countRepairIssues(Long inspectionId) {
        return issueRepository.countByInspectionIdAndStatus(inspectionId,IssueStatus.REPAIR);
    }

    public int countRemovalIssues(Long inspectionId) {
        return issueRepository.countByInspectionIdAndStatus(inspectionId, IssueStatus.REMOVE);
    } // 점검별 remove 이슈

    public List<Issue> getIssuesByInspectionId(Long inspectionId) {
        return issueRepository.findByInspection_Id(inspectionId);
    }



    public Issue addIssue(PublicFa publicFa,InspectionResultDTO.Detection detection) {
        Issue issue = new Issue(publicFa,detection);
        return issueRepository.save(issue);
    }

    public void deleteIssue(IssueDTO issueDTO) {
        issueRepository.deleteById(issueDTO.getId());
    }

    public String uploadResult(MultipartFile file, Long publicFaId) throws IOException {
        ResultReport resultReport = new ResultReport();
        PublicFa pfa = publicFaRepository.findById(publicFaId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(pfa.getIssue() != null){
            resultReport.setIssue(pfa.getIssue());
            resultReport.setCreationDate(new Date());
            String path = azureService.azureBlobUpload(file,".pdf");
            resultReport.setFile(new File(path,"pdf"));
            resultRepository.save(resultReport);
            return azureService.azureBlobSas(path);
        }
        return "해당 시설물에 이슈사항이 없습니다.";
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

