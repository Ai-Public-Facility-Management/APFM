package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.dto.InspectionReportDTO;
import server.dto.InspectionSettingDTO;
import server.dto.InspectionDetailDTO;
import server.service.InspectionReportService;
import server.service.InspectionService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/inspection")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;
    private final InspectionReportService inspectionReportService;

    // ✅ 정기점검 리스트 조회 (페이징 포함)
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getInspectionList(
        @PageableDefault(size = 10, sort = "createDate", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", inspectionService.getInspectionSummary(pageable));
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

    // ✅ 점검 주기 설정 저장
    @PutMapping("/setting")
    public ResponseEntity<Map<String, Object>> saveSetting(@RequestBody InspectionSettingDTO dto) {
        Map<String, Object> response = new HashMap<>();
        response.put("data", inspectionService.setInspectionSetting(dto));
        return ResponseEntity.ok(response);
    }

    // ✅ 점검 보고서 생성 요청 (LLM)
    @PostMapping("/generate")
    public ResponseEntity<String> generateReport(
            @RequestBody InspectionReportDTO requestDTO) {
        inspectionReportService.generateReport(requestDTO);
        return ResponseEntity.ok("");
    }

    // 점검 상세 페이지
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getInspectionDetail(@PathVariable Long id) {
        InspectionDetailDTO data = inspectionService.getInspectionDetail(id);
        Map<String, Object> res = new HashMap<>();
        res.put("data", data);
        return ResponseEntity.ok(res);
    }

}
