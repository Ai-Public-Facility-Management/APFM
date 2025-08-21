package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import server.domain.File;
import server.domain.Inspection;
import server.domain.Issue;
import server.domain.Report;
import server.dto.InspectionReportDTO;
import server.dto.ReportDTO;
import server.repository.InspectionRepository;
import server.repository.IssueRepository;
import server.repository.ReportRepository;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class InspectionReportService {

    private final IssueRepository issueRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final AzureService azureService;
    private final ReportRepository reportRepository;
    private final InspectionRepository inspectionRepository;
    // ✅ 정기점검 보고서 생성
    public void generateReport(InspectionReportDTO requestDTO) throws IOException {
        List<Issue> issues = issueRepository.findAllById(requestDTO.getIssueIds());
        Inspection inspection = inspectionRepository.findById(requestDTO.getInspection_id()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        String fastapiUrl = "http://localhost:8080/priority/run";

        List<ReportDTO> dtos = new ArrayList<>();
        issues.forEach(issue -> {
            ReportDTO dto = new ReportDTO();
            dto.setName(issue.getPublicFa().getType().getDisplayName());
            dto.setDamage(issue.getType().name());
            dto.setEstimated_cost(Math.toIntExact(issue.getEstimate()));
            dto.setHindrance_level(String.valueOf(issue.getPublicFa().getObstruction()));
            dto.setComplaints(0);
            if(issue.getPublicFa().getLastRepair() != null)
                dto.setLast_repair_date(issue.getPublicFa().getLastRepair().toString());
            else
                dto.setLast_repair_date((new Date()).toString());
            dto.setCost_basis(issue.getEstimateBasis());
            dto.setPriority_score(0.0F);
            dtos.add(dto);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("inspection_date",inspection.getCreateDate().toString());
        body.put("facilities",dtos);

        // 4️⃣ HTTP Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 5️⃣ 요청 Entity 생성
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 6️⃣ 요청 전송
        ResponseEntity<Map<String,String>> response =
                restTemplate.exchange(
                        fastapiUrl,
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<>() {
                        }
                );

        Map<String,String> res = response.getBody();
        assert res != null;
        String pdf = res.get("data");

        Report report = new Report();
        report.setCreationDate(inspection.getCreateDate());
        report.setInspection(inspection);
        report = reportRepository.save(report);
        report.setContent(new File(azureService.azureSaveFile(pdf,inspection.getId(),"report"),"정기점검보고서"));
        reportRepository.save(report);

    }
}