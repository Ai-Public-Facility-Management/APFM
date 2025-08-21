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

import java.io.IOException;
import java.util.stream.Collectors;
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
    private final AzureService azureService;

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
                ins.setPublicFaType(issue.getPublicFa().getType().getDisplayName());
                ins.setIssueType(issue.getType() != null ? issue.getType().getDisplayName() : null);
            } else {
                ins.setCameraName(null);
                ins.setPublicFaType(null);
                ins.setIssueType(null);
            }
            dashboardInspections.add(ins);
        });

        return dashboardInspections;
    }

    //점검 주기 설정 저장
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
    public void saveInspectionResult(List<InspectionResultDTO> results) throws IOException {
        // Inspection 생성
        Inspection inspection = new Inspection();
        inspection.setCreateDate(new Date());
        inspection.setIssues(new ArrayList<>());
        inspectionRepository.save(inspection);
        // 리스트 순회하여 저장
        for (InspectionResultDTO dto : results) {
            Camera camera = cameraRepository.findById(dto.getCamera_id()).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));
            camera.setImage(new File(azureService.azureSaveFile(dto.getOriginal_image(),camera.getId(),"camera"),"image"));
            camera = cameraRepository.save(camera);
            for(InspectionResultDTO.Detection detection : dto.getDetections()){
                if(detection.getIssueType().equals("정상")) {
                    PublicFa fa = publicFaService.addPublicFa(camera,detection,"NORMAL");
                    if(fa.getStatus().equals(FacilityStatus.ABNORMAL)){
                        issueService.deleteIssue(fa.getIssue());
                        fa.setIssue(null);
                        fa.setStatus(FacilityStatus.NORMAL);
                    }
                }else{
                    PublicFa fa = publicFaService.addPublicFa(camera,detection,"ABNORMAL");
                    fa.setStatus(FacilityStatus.ABNORMAL);
                    if(fa.getIssue() != null ){
                        if(!fa.getIssue().isProcessing())
                            issueService.updateIssue(fa,detection);

                        return;
                    }
                    Issue issue = issueService.addIssue(fa,detection);
                    fa.setIssue(issue);
                    inspection.getIssues().add(issue);
                    issue.setInspection(inspection);
                }
            }
        }
    }

    // ✅ 점검 상세 조회 (카메라별 이슈 포함)
    @Transactional(readOnly = true)
    public InspectionDetailDTO getInspectionDetail(Long id) {
        Inspection inspection = inspectionRepository.findWithIssuesById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inspection not found"));

        String status = inspection.getReport() != null ? "작성 완료" : "작성중";

        InspectionDetailDTO dto = new InspectionDetailDTO();
        dto.setId(inspection.getId());
        dto.setCreateDate(inspection.getCreateDate() != null
                ? DETAIL_FMT.format(inspection.getCreateDate())
                : null);
        dto.setStatus(status);

        // 카메라별 그룹핑
        List<InspectionDetailDTO.Camera> cameraList = inspection.getIssues().stream()
                .collect(Collectors.groupingBy(issue -> issue.getPublicFa().getCamera()))
                .entrySet().stream()
                .map(entry -> {
                    Camera camera = entry.getKey();
                    List<Issue> cameraIssues = entry.getValue();

                    InspectionDetailDTO.Camera camDto = new InspectionDetailDTO.Camera();
                    camDto.setCameraName(camera.getLocation()); // 또는 camera.getName()
                    if(camera.getImage() != null){
                        camDto.setImageUrl(azureService.azureBlobSas(camera.getImage().getUrl()));
                    }else{
                        camDto.setImageUrl(null);
                    }

                    List<InspectionDetailDTO.IssueItem> issueItems = new ArrayList<>();
                    for (Issue issue : cameraIssues) {
                        InspectionDetailDTO.IssueItem issueItem = new InspectionDetailDTO.IssueItem();
                        issueItem.setId(issue.getId());
                        issueItem.setPublicFaType(issue.getPublicFa().getType().getDisplayName());
                        issueItem.setType(issue.getType() != null ? issue.getType().getDisplayName() : null);
                        issueItem.setEstimate(issue.getEstimate());
                        issueItem.setEstimateBasis(issue.getEstimateBasis());
                        issueItem.setObstruction(issue.getPublicFa().getObstruction());
                        issueItems.add(issueItem);
                    }

                    camDto.setIssues(issueItems);
                    return camDto;
                })
                .collect(Collectors.toList());

        dto.setCameras(cameraList);
        return dto;
    }


}
