package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import server.dto.InspectionReportRequestDTO;
import server.dto.InspectionReportResponseDTO;
import server.service.InspectionReportService;

@RestController
@RequestMapping("/inspection-reports")
@RequiredArgsConstructor
public class InspectionReportController {

    private final InspectionReportService inspectionReportService;

    @PostMapping("/generate")
    public ResponseEntity<InspectionReportResponseDTO> generateReport(
            @RequestBody InspectionReportRequestDTO requestDTO) {
        InspectionReportResponseDTO response = inspectionReportService.generateReport(requestDTO.getIssueIds());
        return ResponseEntity.ok(response);
    }
}