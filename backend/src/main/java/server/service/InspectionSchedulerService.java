// 📄 server.service.InspectionSchedulerService.java

package server.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import server.domain.Camera;
import server.domain.InspectionSetting;
import server.dto.InspectionResultDTO;
import server.repository.CameraRepository;
import server.repository.InspectionSettingRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionSchedulerService {

    private final InspectionSettingRepository inspectionSettingRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final InspectionService inspectionService;
    private final CameraRepository  cameraRepository;
    private final AzureService azureService;

    @PostConstruct
    public void init() {
        if(inspectionSettingRepository.count() == 0) {
            inspectionSettingRepository.saveAndFlush(new InspectionSetting());
        }
    }

    // ✅ [1] 주기적으로 점검 수행 (매시 정각 실행)
    @Scheduled(cron = "0 0 * * * *")
    public void performScheduledInspections() {
        log.info("🕒 주기적 점검 시작");

        InspectionSetting setting = inspectionSettingRepository.findById(1L).orElseThrow();
        LocalDateTime now = LocalDateTime.now();


        if (isDue(setting, now)) {
            // FastAPI 요청
            callDetect();
            // 마지막 점검일 갱신
            setting.setLastInspectedDate(now);
            inspectionSettingRepository.save(setting);
        }

    }

    // ✅ [2] 점검 도래 여부: 마지막 점검일 + 주기 ≤ 현재
    private boolean isDue(InspectionSetting setting, LocalDateTime now) {
        if (setting.getInspectionCycle() == null) return false;

        LocalDateTime last = setting.getLastInspectedDate();
        if (last == null) return true;

        return !now.isBefore(last.plusDays(setting.getInspectionCycle()));
    }

    // ✅ [3] FastAPI 점검 요청
    public void callDetect() {
        try {
            // 1️⃣ FastAPI 서버 URL
            List<Long> camera_ids = cameraRepository.findAll().stream().map(Camera::getId).collect(Collectors.toList());
            camera_ids = azureService.getVideos(camera_ids);
            String fastapiUrl = "http://localhost:8080/predict";

            // 3️⃣ Multipart Form 구성
            Map<String, Object> body = new HashMap<>();
            body.put("camera_ids", camera_ids);

            // 4️⃣ HTTP Header 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 5️⃣ 요청 Entity 생성
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    

            ResponseEntity<List<InspectionResultDTO>> response =
                    restTemplate.exchange(
                            fastapiUrl,
                            HttpMethod.POST,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            }
                    );

            log.info("✅ FastAPI 응답 수신 : {}", response.getBody());
            List<InspectionResultDTO> dtos = response.getBody();
            assert dtos != null;
            inspectionService.saveInspectionResult(dtos);
    
        } catch (Exception e) {
            log.error("❌ FastAPI 호출 실패 : {}",e.getMessage());
    }
}
}
