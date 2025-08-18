package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InspectionReportService {

    private final IssueRepository issueRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final AzureService azureService;
    private final ReportRepository reportRepository;
    private final InspectionRepository inspectionRepository;
    // ✅ 정기점검 보고서 생성
    public void generateReport(InspectionReportDTO requestDTO) {
        List<Issue> issues = issueRepository.findAllById(requestDTO.getIssueIds());
        Inspection inspection = inspectionRepository.findById(requestDTO.getInspection_id()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        String fastapiUrl = "http://localhost:8000/priority/run";
        // 3️⃣ Multipart Form 구성
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("inspection_date",inspection.getCreateDate());

        List<ReportDTO> dtos = new ArrayList<>();
        issues.forEach(issue -> {
            ReportDTO dto = new ReportDTO();
            dto.setName(issue.getPublicFa().getType().name());
            dto.setDamage(issue.getType().name());
            dto.setEstimated_cost(Math.toIntExact(issue.getEstimate()));
            dto.setHindrance_level(String.valueOf(issue.getObstruction()));
            dto.setComplaints(0);
            dto.setLast_repair_date(issue.getPublicFa().getLastRepair());
            dto.setCost_basis(issue.getEstimateBasis());
            dto.setPriority_score(0.0F);
            dtos.add(dto);
        });

        body.add("facilities",dtos);

        // 4️⃣ HTTP Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 5️⃣ 요청 Entity 생성
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 6️⃣ 요청 전송
        ResponseEntity<String> response = restTemplate.postForEntity(fastapiUrl, requestEntity, String.class);
        Report report = new Report();
        report.setCreationDate(inspection.getCreateDate());
        report.setInspection(inspection);
        report.setContent(new File(response.getBody(),"정기점검보고서"));
        reportRepository.save(report);

    }
}