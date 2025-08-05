package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import server.domain.*;
import server.repository.*;
import server.dto.*;

@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final InspectionSettingRepository settingRepository;

    // ✅ 정기점검 리스트 조회 (페이징 포함)
    public Page<InspectionSummary> getInspectionListResponse(Pageable pageable) {
        Page<Inspection> inspections = inspectionRepository.findAll(pageable);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        return inspections.map(inspection -> {
            Long inspectionId = inspection.getId();
            String formattedDate = formatter.format(inspection.getCreateDate());
            List<Issue> issues = inspection.getIssues();

            int repairCount = (int) issues.stream()
                .filter(i -> i.getType() == IssueType.REPAIR)
                .count();

            int removalCount = (int) issues.stream()
                .filter(i -> i.getType() == IssueType.REMOVE)
                .count();

            boolean hasIssue = !issues.isEmpty();
            boolean hasReport = inspection.getReport() != null;

            if (inspection.getIsinspected()) {
                return new InspectionSummary(
                        inspectionId,
                        formattedDate,
                        true,
                        repairCount,
                        removalCount,
                        hasIssue,
                        hasReport
                );
            }
            return new InspectionSummary(
                    inspectionId,
                    formattedDate,
                    false,
                    repairCount,
                    removalCount,
                    hasIssue,
                    hasReport
            );
        });
    }

    // ✅ 점검 리스트 조회 (메인페이지 대쉬보드용)
    public List<DashboardInspection> getDashboardInspections(int count) {
        PageRequest pageRequest = PageRequest.of(0, count);
        List<Inspection> inspections = inspectionRepository.findByIsinspectedTrueOrderByCreateDateDesc(pageRequest);
        List<DashboardInspection> dashboardInspections = new ArrayList<>();
        for (Inspection inspection : inspections) {
            DashboardInspection ins =  new DashboardInspection();

            ins.setInspectionId(inspection.getId());
            ins.setInspectionDate(inspection.getCreateDate());

            if(inspection.getIssues() != null ) {
                List<Issue> issues = inspection.getIssues();
                Issue issue = issues.get(issues.size()-1);
                ins.setCameraName(issue.getPublicFa().getCamera().getLocation());
                ins.setPublicFaType(issue.getPublicFa().getType());
                ins.setIssueType(issue.getType());
            }
            else{
                ins.setCameraName(null);
                ins.setPublicFaType(null);
                ins.setIssueType(null);
            }

            dashboardInspections.add(ins);
        }


        return dashboardInspections;
    }

    @Transactional
    public InspectionSettingDTO setInspectionSetting(InspectionSettingDTO dto) {
        InspectionSetting setting = settingRepository.findById(1L).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
        setting.setInspectionCycle(dto.getInspectionCycle());
        setting.setAddress(dto.getAddress());
        setting.setStartTime(dto.getStartTime());
        setting.setStartDate(dto.getStartDate());
        settingRepository.save(setting);
        return dto;
    }

    
}
