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

    private static final SimpleDateFormat DETAIL_FMT = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");


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
                    hasIssue);
        });
    }

    // ✅ 점검 리스트 조회 (메인페이지 대쉬보드용)
    public List<DashboardInspection> getDashboardInspections(int count) {
        PageRequest pageRequest = PageRequest.of(0, count);
        List<Inspection> inspections = inspectionRepository.findByOrderByCreateDateDesc(pageRequest);
        List<DashboardInspection> dashboardInspections = new ArrayList<>();
        inspections.forEach(inspection -> {
            DashboardInspection ins = new DashboardInspection();
            ins.setInspectionId(inspection.getId());
            ins.setInspectionDate(inspection.getCreateDate());
            if (!inspection.getIssues().isEmpty()) {
                List<Issue> issues = inspection.getIssues();
                Issue issue = issues.get(issues.size() - 1);
                ins.setCameraName(issue.getPublicFa().getCamera().getLocation());
                ins.setPublicFaType(issue.getPublicFa().getType());
                ins.setIssueType(issue.getType());
            } else {
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
        InspectionSetting setting = settingRepository.findById(1L)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        setting.setInspectionCycle(dto.getInspectionCycle());
        // setting.setAddress(dto.getAddress());
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
        // 리스트 순회하여 저장
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

    // 점검 상세 추가
    @Transactional(readOnly = true)
    public InspectionDetailDTO getInspectionDetail(Long id) {
        Inspection inspection = inspectionRepository.findWithIssuesById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspection not found"));

        String status = inspection.getReport() != null ? "작성 완료" : "작성중";

        InspectionDetailDTO dto = new InspectionDetailDTO();
        dto.setId(inspection.getId());
        dto.setCreateDate(inspection.getCreateDate() != null ? DETAIL_FMT.format(inspection.getCreateDate()) : null);
        dto.setStatus(status);

        // (옵션) 상단 요약 필드 - 현재 도메인에 따라 값이 없을 수 있음 → 일단 null
        dto.setFacilityName(null);
        dto.setLocation(null);

        // (옵션) 본문/요약 - 도메인에 있다면 매핑하세요. 없으면 null 유지
        dto.setDescription(null);
        dto.setContent(null);

        // (옵션) 상단 이미지 목록 - 도메인에 저장된 리스트가 있으면 매핑. 지금은 빈 리스트
        dto.setImageUrlList(List.of());

        // 이슈 매핑
        List<InspectionDetailDTO.IssueItem> items = new ArrayList<>();
        if (inspection.getIssues() != null) {
            for (Issue issue : inspection.getIssues()) {
                InspectionDetailDTO.IssueItem it = new InspectionDetailDTO.IssueItem();
                it.setId(issue.getId());
                it.setFacilityCategory(issue.getPublicFa() != null ? issue.getPublicFa().getType().name() : null);
                it.setType(issue.getType() != null ? issue.getType().name() : null);
                it.setStatus(issue.getStatus() != null ? issue.getStatus().name() : null);

                // 발생도/레벨/카운트 값이 도메인에 없다면 일단 null로 둠
                it.setSeverity(null);
                it.setLevel(null);
                it.setCount(null);

                it.setEstimate(issue.getEstimate());
                it.setEstimateBasis(issue.getEstimateBasis());
                it.setDescription(issue.getDescription());
                it.setContent(issue.getContent());

                // 임베디드 Photo가 있다면 URL 매핑
                if (issue.getImage() != null) {
                    it.setImageUrl(issue.getImage().getUrl());
                } else {
                    it.setImageUrl(null);
                }

                // FastAPI로부터 받은 상대 경로가 따로 있다면 여기에 매핑
                it.setAiImagePath(null);

                items.add(it);
            }
        }
        dto.setIssues(items);

        return dto;
    }


}
