package server.controller;

import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import server.domain.*;
import server.service.*;

import java.util.Map;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping("/inspections")
@Transactional
@RequiredArgsConstructor
public class InspectionController {

    private final InspectionService inspectionService;


    @PostMapping
    public Inspection create(@RequestBody Inspection inspection) {
        // Date → LocalDateTime 변환
        Date date = inspection.getCreateDate();
        LocalDateTime convertedDate = date.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();

    return inspectionService.createInspection(
        convertedDate,
        inspection.getIsinspected()
    );
}

    @GetMapping
    public Iterable<Inspection> getAll() {
        return inspectionService.getAllInspections();
    }

    @GetMapping("/{id}")
    public Optional<Inspection> getOne(@PathVariable("id") Long id) {
        return inspectionService.getInspection(id);
    }

    @PutMapping("/{id}")
    public Inspection update(@PathVariable("id") Long id, @RequestBody Inspection updated) {
        return inspectionService.updateInspection(id, updated.getIsinspected());
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        inspectionService.deleteInspection(id);
    }

    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getPagedInspections(
        @PageableDefault(size = 10, sort = "createDate", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return inspectionService.getInspectionListResponse(pageable);// 프론트에서 /inspections/page?page=0&size=10 로 호출하면 createDate 기준 최신순으로 페이징된 점검 리스트가 반환됨
    } 

    @GetMapping("/{id}/unmatched-facilities")
    public ResponseEntity<Boolean> checkUnmatchedFacilities(@PathVariable Long id) {
        boolean hasUnmatched = inspectionService.hasUnmatchedFacilities(id);
        return ResponseEntity.ok(hasUnmatched);
    }  // GET /inspections/3/unmatched-facilities 이런 식으로 호출 시 Boolean(true, false) 반환 api
       // true면 매칭 안되는 시설물이 있어서 모달띄움, false면 바로 상세 페이지

    @GetMapping("/{id}/detail")
    public ResponseEntity<InspectionDetailResponseDTO> getInspectionDetail(@PathVariable Long id) {
        return ResponseEntity.ok(inspectionService.buildInspectionDetailResponse(id));
    }
}
//>>> Clean Arch / Inbound Adaptor
