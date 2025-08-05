package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.dto.InspectionReportResponseDTO;
import server.domain.Issue;
import server.repository.IssueRepository;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InspectionReportService {

    private final IssueRepository issueRepository;

    public InspectionReportResponseDTO generateReport(List<Long> issueIds) {
        List<Issue> issues = issueRepository.findAllById(issueIds);

        // LLM으로부터 받아온다고 가정
        String content = "=== 정기 점검 보고서 ===\n" +
            issues.stream()
                  .map(i -> "- " + i.getEstimateBasis())
                  .collect(Collectors.joining("\n"));

        // 파일명만 생성
        String fileName = "inspection_report_" + System.currentTimeMillis() + ".txt";

        // 반환
        InspectionReportResponseDTO response = new InspectionReportResponseDTO();
        response.setFileName(fileName);
        response.setContent(content);
        return response;
    }

}