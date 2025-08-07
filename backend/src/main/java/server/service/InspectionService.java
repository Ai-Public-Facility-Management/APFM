package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import server.domain.*;
import server.dto.*;
import server.repository.*;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final InspectionSettingRepository settingRepository;
    private final IssueService issueService;
    private final PublicFaService publicFaService;

    // ✅ 정기점검 리스트 조회 (페이징 포함)
    public Page<InspectionSummary> getInspectionSummary(Pageable pageable) {
        Page<Inspection> inspections = inspectionRepository.findAll(pageable);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        return inspections.map(inspection -> {
            Long inspectionId = inspection.getId();
            String formattedDate = formatter.format(inspection.getCreateDate());

            // Issue 조회
            List<Issue> issues = inspection.getIssues();

            int repairCount = (int) issues.stream()
                .filter(i -> i.getStatus() == IssueStatus.REPAIR)
                .count();

            int removalCount = (int) issues.stream()
                .filter(i -> i.getStatus() == IssueStatus.REMOVE)
                .count();

            boolean hasIssue = !issues.isEmpty();
            boolean hasReport = inspection.getReport() != null;

            // 상태 판별
            Boolean isInspected = inspection.getIsinspected();
            String status = Boolean.TRUE.equals(inspection.getIsinspected()) ? "작성 완료" : "작성중";

            return new InspectionSummary(
                inspectionId,
                formattedDate,
                status,
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

    // ✅ FastAPI 응답 결과를 저장하는 메서드
    @Transactional
    public void saveInspectionResult(List<InspectionResultDTO> results) {

//        // 1️⃣ 사용자 조회
//        Users user = usersRepository.findByEmail(result.getEmail())
//            .orElseThrow(() -> new IllegalArgumentException("사용자 이메일이 존재하지 않습니다: " + result.getEmail()));

        // Inspection 생성
        Inspection inspection = new Inspection();
        inspection.setCreateDate(new Date());
        inspection.setReportUrl(null);
        inspection.setIsinspected(true); // 점검 완료 상태
        inspectionRepository.save(inspection);

        //리스트 순회하여 저장
        for (InspectionResultDTO dto : results) {
            if(dto.getDetections().getStatus().equals("NOMAL")) {
                publicFaService.addPublicFa(dto.getDetections().getCameraId(), dto.getDetections().getPublicFaType(), dto.getDetections().getBox(), "NORMAL");
            }else{
                PublicFa publicFa = publicFaService.addPublicFa(dto.getDetections().getCameraId(), dto.getDetections().getPublicFaType(), dto.getDetections().getBox(), "ABNORMAL");
                Issue issue = issueService.addIssue(dto.getDetections().getStatus(),1L,dto.getDetections().getCost_estimate(),dto.getOriginal_image(),publicFa,inspection);
                publicFa.setIssue(issue);
                inspection.getIssues().add(issue);
            }

        }
    }

    
}
