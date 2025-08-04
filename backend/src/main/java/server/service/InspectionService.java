package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import server.domain.*;
import server.repository.*;
import server.dto.*;
import server.service.*;

@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final IssueService issueService;

    // ✅ 점검 리스트 조회 (UI 리스트용)
    public ResponseEntity<Map<String, Object>> getInspectionListResponse(Pageable pageable) {
        Page<Inspection> inspections = inspectionRepository.findAll(pageable);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        List<InspectionListResponseDTO> dtos = inspections.getContent().stream().map(inspection -> {
            String formattedDate = formatter.format(inspection.getCreateDate());

            int repairCount = issueService.countRepairIssues(inspection.getId());
            int removalCount = issueService.countRemovalIssues(inspection.getId());

            boolean hasIssue = (repairCount + removalCount) > 0;
            boolean hasReport = false; // reportService.existsByInspectionId(inspection.getId());

            return new InspectionListResponseDTO(
                inspection.getId(),
                formattedDate,
                inspection.getIsinspected(),
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

    // ✅ 점검 리스트 조회 (메인페이지 리스트용)
    public DashboardInspectionResponseDTO getDashboardInspections(int count) {
        List<Inspection> inspections = inspectionRepository
            .findTopNByIsInspectedTrueOrderByCreateDateDesc(count); // 또는 Pageable 방식

        List<String> dateList = new ArrayList<>();
        Map<String, List<DashboardInspectionResponseDTO.IssueDTO>> issuesMap = new HashMap<>();

        for (Inspection inspection : inspections) {
            String dateStr = formatDate(inspection.getCreateDate());
            dateList.add(dateStr);

            List<Issue> issues = issueRepository.findByInspectionId(inspection.getId());
            List<DashboardInspectionResponseDTO.IssueDTO> issueDTOs = issues.stream().map(issue -> {
                DashboardInspectionResponseDTO.IssueDTO dto = new DashboardInspectionResponseDTO.IssueDTO();
                dto.setId(issue.getId());
                dto.setContent(issue.getContent());
                return dto;
            }).collect(Collectors.toList());

            issuesMap.put(dateStr, issueDTOs);
        }

        DashboardInspectionResponseDTO response = new DashboardInspectionResponseDTO();
        response.setInspectionDate(dateList);
        response.setIssues(issuesMap);

        return response;
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy.MM.dd").format(date);
    }

    // ✅ 점검 생성 (자동 생성용)
    public Inspection createInspection(LocalDateTime createDate, Boolean isInspected) {
        Inspection inspection = new Inspection();
        Date converted = Date.from(createDate.atZone(ZoneId.systemDefault()).toInstant());
        inspection.setCreateDate(converted);
        inspection.setIsinspected(isInspected);
        return inspectionRepository.save(inspection);
    }
}
