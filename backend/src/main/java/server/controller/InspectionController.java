package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import server.service.*;
import server.dto.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    // ✅ 정기점검 리스트 조회 (페이징 포함)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getInspectionList(
        @PageableDefault(size = 10, sort = "createDate", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return inspectionService.getInspectionListResponse(pageable);
    }

    // ✅ 정기점검 리스트 조회 (메인페이지)
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardInspectionResponseDTO> getDashboardInspections(
        @RequestParam(defaultValue = "5") int count) {
        return ResponseEntity.ok(inspectionService.getDashboardInspections(count));
    }

}
