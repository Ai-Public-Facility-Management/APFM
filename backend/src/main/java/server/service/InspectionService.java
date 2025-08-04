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

@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final IssueService issueService;
    private final IssueRepository issueRepository; // hasReport 및 직접 issue count 조회용

    // ✅ 정기점검 리스트 조회 (페이징 포함)
    public ResponseEntity<Map<String, Object>> getInspectionListResponse(Pageable pageable) {
        Page<Inspection> inspections = inspectionRepository.findAll(pageable);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        List<InspectionListResponseDTO> dtos = inspections.getContent().stream().map(inspection -> {
            Long inspectionId = inspection.getId();
            String formattedDate = formatter.format(inspection.getCreateDate());

            // Issue 조회
            List<Issue> issues = issueRepository.findByInspectionId(inspectionId);

            int repairCount = (int) issues.stream()
                .filter(i -> i.getType() == IssueType.REPAIR)
                .count();

            int removalCount = (int) issues.stream()
                .filter(i -> i.getType() == IssueType.REMOVE)
                .count();

            boolean hasIssue = !issues.isEmpty();
            boolean hasReport = inspection.getReport() != null;

            // 상태 판별
            String status;
            if (!inspection.getIsinspected()) status = "작성중";
            else status = "작성 완료";

            return new InspectionListResponseDTO(
                inspectionId,
                formattedDate,
                status,
                repairCount,
                removalCount,
                hasIssue,
                hasReport
            );
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("data", dtos); // ✅ 프론트 API 스펙 맞춤
        response.put("totalPages", inspections.getTotalPages());
        response.put("currentPage", inspections.getNumber() + 1);

        return ResponseEntity.ok(response);
    }

    // ✅ 점검 리스트 조회 (메인페이지 대쉬보드용)
    public DashboardInspectionResponseDTO getDashboardInspections(int count) {
        // 1. 최근 count개의 작성 완료된 점검 불러오기 (createDate 기준 내림차순)
        Pageable pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "createDate"));
        List<Inspection> inspections = inspectionRepository.findByIsinspectedTrueOrderByCreateDateDesc(pageable);

        List<String> dateList = new ArrayList<>();
        Map<String, List<DashboardInspectionResponseDTO.IssueSummaryDTO>> issuesMap = new HashMap<>();

        for (Inspection inspection : inspections) {
            String dateStr = formatDate(inspection.getCreateDate());
            dateList.add(dateStr);

            // 2. 점검에 속한 이슈들 조회
            List<Issue> issues = issueRepository.findByInspection_Id(inspection.getId());
 
            // 3. 이슈를 IssueStatus 기준으로 그룹핑하여 개수 세기
            Map<IssueStatus, Long> countByStatus = issues.stream()
                .collect(Collectors.groupingBy(Issue::getStatus, Collectors.counting()));

            // 4. 이 정보를 IssueSummaryDTO 리스트로 변환
            List<DashboardInspectionResponseDTO.IssueSummaryDTO> summaries = countByStatus.entrySet().stream()
                .map(entry -> {
                    DashboardInspectionResponseDTO.IssueSummaryDTO dto = new DashboardInspectionResponseDTO.IssueSummaryDTO();
                    dto.setStatus(entry.getKey());
                    dto.setCount(entry.getValue().intValue());
                    return dto;
                }).collect(Collectors.toList());

            issuesMap.put(dateStr, summaries);
        }

        // 5. 최종 응답 DTO 조립
        DashboardInspectionResponseDTO response = new DashboardInspectionResponseDTO();
        response.setInspectionDate(dateList);
        response.setIssues(issuesMap);

        return response;
    }

    // 날짜 포맷 변환용 유틸
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
