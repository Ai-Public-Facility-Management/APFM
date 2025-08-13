package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import server.domain.*;
import server.dto.*;
import server.repository.CameraRepository;
import server.repository.InspectionRepository;
import server.repository.InspectionSettingRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final InspectionSettingRepository settingRepository;
    private final IssueService issueService;
    private final PublicFaService publicFaService;
    private final CameraRepository cameraRepository;

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

            // 상태 판별
            String status = inspection.getReport() != null ? "작성 완료" : "작성중";

            return new InspectionSummary(
                inspectionId,
                formattedDate,
                status,
                repairCount,
                removalCount,
                hasIssue
            );
        });
    }

    // ✅ 점검 리스트 조회 (메인페이지 대쉬보드용)
    public List<DashboardInspection> getDashboardInspections(int count) {
        PageRequest pageRequest = PageRequest.of(0, count);
        List<Inspection> inspections = inspectionRepository.findByOrderByCreateDateDesc(pageRequest);
        List<DashboardInspection> dashboardInspections = new ArrayList<>();
        inspections.forEach(inspection -> {
            DashboardInspection ins =  new DashboardInspection();
            ins.setInspectionId(inspection.getId());
            ins.setInspectionDate(inspection.getCreateDate());
            if(!inspection.getIssues().isEmpty()) {
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
        });

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

        // Inspection 생성
        Inspection inspection = new Inspection();
        inspection.setCreateDate(new Date());
        inspectionRepository.save(inspection);

        //리스트 순회하여 저장
        for (InspectionResultDTO dto : results) {
            Camera camera = cameraRepository.findById(dto.getCameraId()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
            camera.setImage(dto.getOriginal_image(),"image");
            camera = cameraRepository.save(camera);
            for(InspectionResultDTO.Detection detection : dto.getDetections()){
                if(detection.getIssueType().equals("NONE")) {
                    publicFaService.addPublicFa(camera,detection,"NORMAL");
                }else{
                    PublicFa publicFa = publicFaService.addPublicFa(camera,detection,"ABNORMAL");
                    Issue issue = issueService.addIssue(publicFa,detection);
                    publicFa.setIssue(issue);
                    inspection.getIssues().add(issue);
                }
            }


        }
    }

    
}
