package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import server.domain.InspectionSetting;
import server.repository.InspectionSettingRepository;
import server.service.*;
import server.dto.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inspection")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;
    private final InspectionSettingRepository inspectionSettingRepository;
    private final InspectionReportService inspectionReportService;

    // ✅ 정기점검 리스트 조회 (페이징 포함)
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getInspectionList(
        @PageableDefault(size = 10, sort = "createDate", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", inspectionService.getInspectionListResponse(pageable));
        return ResponseEntity.ok(response);
    }

    // ✅ 정기점검 리스트 조회 (메인페이지)
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardInspections(
        @RequestParam(defaultValue = "5") int count) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", inspectionService.getDashboardInspections(count));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/setting")
    public ResponseEntity<Map<String, Object>> saveSetting(@RequestBody InspectionSettingDTO dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", inspectionService.setInspectionSetting(dto));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/generate")
    public ResponseEntity<InspectionReportResponseDTO> generateReport(
            @RequestBody InspectionReportRequestDTO requestDTO) {
        InspectionReportResponseDTO response = inspectionReportService.generateReport(requestDTO.getIssueIds());
        return ResponseEntity.ok(response);
    }

}
