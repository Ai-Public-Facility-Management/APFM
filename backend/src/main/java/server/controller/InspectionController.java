package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;

import server.service.InspectionService;

import java.util.Map;

@RestController
@RequestMapping("/api/inspections")
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;

    // ✅ 정기점검 리스트 조회 (페이징 포함)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getInspectionList(Pageable pageable) {
        return inspectionService.getInspectionListResponse(pageable);
    }
}
