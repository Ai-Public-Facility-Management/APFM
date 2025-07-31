package server.service;

import lombok.RequiredArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

import server.domain.Inspection;
import server.repository.InspectionRepository;
import server.dto.InspectionListResponseDTO;

@Data
@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final IssueService issueService;
    // private final ReportService reportService;

    public ResponseEntity<Map<String, Object>> getInspectionListResponse(Pageable pageable) {
        Page<Inspection> inspections = inspectionRepository.findAll(pageable);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        List<InspectionListResponseDTO> dtos = inspections.getContent().stream().map(inspection -> {
            String formattedDate = formatter.format(inspection.getCreateDate());
            String status = inspection.getIsinspected() ? "작성 완료" : "작성중";

            // TODO: 실제 서비스 연결
            int repairCount = issueService.countRepairIssues(inspection.getId());
            int removalCount = issueService.countRemovalIssues(inspection.getId());
            boolean hasIssue = (repairCount + removalCount) == 0;
            boolean hasReport = false;  // boolean hasReport = reportService.existsByInspectionId(inspection.getId()); report 생성되면 교체


            return new InspectionListResponseDTO(
                inspection.getId(),
                formattedDate,
                status,
                repairCount,
                removalCount,
                hasIssue,
                hasReport
            );
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("content", dtos);
        response.put("totalPages", inspections.getTotalPages());
        response.put("currentPage", inspections.getNumber());

        return ResponseEntity.ok(response);
    }

    public Inspection createInspection(LocalDateTime createDate, Boolean isInspected) {
        Inspection inspection = new Inspection();
        Date converted = Date.from(createDate.atZone(ZoneId.systemDefault()).toInstant());
        inspection.setCreateDate(converted);
        inspection.setIsinspected(isInspected);
        return inspectionRepository.save(inspection);
    }

    public List<Inspection> getAllInspections() {
        return inspectionRepository.findAll();
    }

    public Optional<Inspection> getInspection(Long id) {
        return inspectionRepository.findById(id);
    }

    public Inspection updateInspection(Long id, Boolean isInspected) {
        Inspection inspection = inspectionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 점검이 없습니다."));
        inspection.setIsinspected(isInspected);
        return inspectionRepository.save(inspection);
    }

    public void deleteInspection(Long id) {
        inspectionRepository.deleteById(id);
    }
}
