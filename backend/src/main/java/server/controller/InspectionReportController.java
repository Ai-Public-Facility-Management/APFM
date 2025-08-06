package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.dto.InspectionReportDTO;
import server.service.InspectionReportService;

@RestController
@RequestMapping("/inspection-reports")
@RequiredArgsConstructor
public class InspectionReportController {

    private final InspectionReportService inspectionReportService;

    @PostMapping("/generate")
    public ResponseEntity<InspectionReportDTO> generateReport(@RequestBody InspectionReportDTO requestDTO) {
        // requestDTO 에서 issueIds만 추출해서 전달
        InspectionReportDTO response = inspectionReportService.generateReport(requestDTO.getIssueIds());
        return ResponseEntity.ok(response);
    }
}