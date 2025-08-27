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
import java.time.LocalDate;
import java.time.LocalTime;


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

    // ✅ 1분마다 점검 주기 확인
    @Scheduled(fixedRate = 60000)
    public void performScheduledInspections() {
        log.info("⏰ 점검 주기 검사 실행");
        InspectionSetting setting = inspectionSettingRepository.findById(1L).orElseThrow();
        LocalDateTime now = LocalDateTime.now();

        log.debug("🔍 설정값 확인: cycle={}, startTime={}, lastDate={}",
                setting.getInspectionCycle(),
                setting.getStartTime(),
                setting.getLastInspectedDate());

        log.info("🔍 설정값 확인: cycle={}, startTime={}, lastDate={}",
                setting.getInspectionCycle(),
                setting.getStartTime(),
                setting.getLastInspectedDate());

        if (isDue(setting, now)) {
            log.info("✅ 점검 실행됨: {}", now);
            callDetect();

            // ✅ 실행 후 lastInspectedDate는 이번 실행 시각으로 저장
            LocalDateTime executedTime = now.toLocalDate()
                    .atTime(LocalTime.parse(setting.getStartTime())).plusHours(9);
            setting.setLastInspectedDate(executedTime);
            inspectionSettingRepository.save(setting);
        }
    }

    // ✅ 주기 + 시:분 단위 검사
    private boolean isDue(InspectionSetting setting, LocalDateTime now) {
        if (setting.getInspectionCycle() == null) return false;

        LocalTime startTime = LocalTime.parse(setting.getStartTime());

        // 아직 실행한 적 없음 → 오늘 설정한 시각에 실행
        if (setting.getLastInspectedDate() == null) {
            LocalDateTime scheduledTime = now.toLocalDate().atTime(startTime);
            boolean result = !now.isBefore(scheduledTime) && now.isBefore(scheduledTime.plusMinutes(1));
            log.debug("🟡 [isDue-init] now={}, scheduledTime={}, result={}", now, scheduledTime, result);
            return result;
        }

        // 마지막 점검일 + 주기
        LocalDate nextDate = setting.getLastInspectedDate().toLocalDate()
                .plusDays(setting.getInspectionCycle());

        LocalDateTime nextScheduledTime = nextDate.atTime(startTime);

        boolean result = !now.isBefore(nextScheduledTime) && now.isBefore(nextScheduledTime.plusMinutes(1));
        log.debug("🟢 [isDue] now={}, nextScheduledTime={}, result={}", now, nextScheduledTime, result);
        return result;
    }


    // ✅ [3] FastAPI 점검 요청
    public void callDetect() {
        try {
            // 1️⃣ FastAPI 서버 URL
            List<Long> camera_ids = cameraRepository.findAll().stream().map(Camera::getId).collect(Collectors.toList());
            camera_ids = azureService.getVideos(camera_ids);
            String fastapiUrl = "http://fastapi:8080/predict";

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
