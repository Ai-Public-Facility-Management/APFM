package server.controller;

import java.util.Optional;


import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import server.domain.*;
import server.service.InspectionService;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
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
    public Page<Inspection> getPagedInspections(
        @PageableDefault(size = 10, sort = "createDate", direction = Sort.Direction.DESC)
        Pageable pageable
    ) {
        return inspectionService.getPagedInspections(pageable);
    }  // 프론트에서 /inspections/page?page=0&size=10 로 호출하면 createDate 기준 최신순으로 페이징된 점검 리스트가 반환됨
}
//>>> Clean Arch / Inbound Adaptor
