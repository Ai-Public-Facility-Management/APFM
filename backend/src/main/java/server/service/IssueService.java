package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import server.domain.File;
import server.domain.Issue;
import server.domain.PublicFa;
import server.domain.ResultReport;
import server.dto.InspectionResultDTO;
import server.dto.IssueDetail;
import server.repository.IssueRepository;
import server.repository.PublicFaRepository;
import server.repository.ResultReportRepository;

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
    private AzureService azureService;
    @Autowired
    private ResultReportRepository resultRepository;



    public void deleteIssue(Issue issue){
        if(issue != null){
            issueRepository.deleteById(issue.getId());
        }
    }

    public void updateIssue(PublicFa fa, InspectionResultDTO.Detection detection) {
        issueRepository.save(fa.getIssue().update(fa, detection));
    }
    public Issue addIssue(PublicFa fa,InspectionResultDTO.Detection detection) {
        return issueRepository.save(new Issue(fa,detection));
    }


    public String uploadResult(MultipartFile file, Long publicFaId) throws IOException {
        ResultReport resultReport = new ResultReport();
        PublicFa pfa = publicFaRepository.findById(publicFaId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        if(pfa.getIssue() != null){
            resultReport.setIssue(pfa.getIssue());
            resultReport.setCreationDate(new Date());
            String path = azureService.azureBlobUpload(file,".pdf");
            resultReport.setFile(new File(path,"pdf"));
            pfa.setLastRepair(new Date());
            resultRepository.save(resultReport);
            pfa.getIssue().setResultReport(resultReport);
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


}

