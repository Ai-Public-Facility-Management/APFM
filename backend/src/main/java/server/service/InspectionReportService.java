package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import server.dto.InspectionReportDTO;
import server.domain.Issue;
import server.repository.IssueRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InspectionReportService {

    private final IssueRepository issueRepository;

    // ✅ 정기점검 보고서 생성
    public InspectionReportDTO generateReport(List<Long> issueIds) {
//        List<Issue> issues = issueRepository.findAllById(issueIds);
//
//        // LLM 기반으로 생성된 보고서 내용 (모의)
//        String content = "=== 정기 점검 보고서 ===\n" +
//            issues.stream()
//                  .map(i -> "- " + String.valueOf(i.getContent()))
//                  .collect(Collectors.joining("\n"));
//
//        // 파일 이름 생성
//        String fileName = "inspection_report_" + System.currentTimeMillis() + ".txt";

        // DTO에 결과 세팅 후 반환
        InspectionReportDTO response = new InspectionReportDTO();
//        response.setIssueIds(issueIds);
//        response.setFileName(fileName);
//        response.setContent(content);
        return response;
    }
}