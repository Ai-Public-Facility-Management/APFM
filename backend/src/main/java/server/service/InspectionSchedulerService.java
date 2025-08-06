// 📄 server.service.InspectionSchedulerService.java

package server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import server.domain.InspectionSetting;
import server.repository.InspectionSettingRepository;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionSchedulerService {

    private final InspectionSettingRepository inspectionSettingRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ [1] 주기적으로 점검 수행 (매시 정각 실행)
    @Scheduled(cron = "0 0 * * * *")
    public void performScheduledInspections() {
        log.info("🕒 주기적 점검 시작");

        List<InspectionSetting> allSettings = inspectionSettingRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (InspectionSetting setting : allSettings) {
            if (isDue(setting, now)) {
                // FastAPI 요청
                callFastApi(setting.getEmail());

                // 마지막 점검일 갱신
                setting.setLastInspectedDate(now);
                inspectionSettingRepository.save(setting);
            }
        }
    }

    // ✅ [2] 점검 도래 여부: 마지막 점검일 + 주기 ≤ 현재
    private boolean isDue(InspectionSetting setting, LocalDateTime now) {
        if (setting.getInspectionCycle() == null) return false;

        LocalDateTime last = setting.getLastInspectedDate();
        if (last == null) return true;  // 첫 점검

        return !now.isBefore(last.plusDays(setting.getInspectionCycle()));
    }

    // ✅ [3] FastAPI 점검 요청
    public void callFastApi(String email) {
        String fastapiUrl = "http://localhost:8000/ai/inspect";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(fastapiUrl, entity, String.class);
            log.info("✅ FastAPI 응답 수신 ({}): {}", email, response.getBody());

            // 📌 필요 시 response.getBody() -> DTO 변환 + 저장
            // InspectionResultDTO dto = objectMapper.readValue(response.getBody(), InspectionResultDTO.class);
            // inspectionService.saveInspectionResult(dto);

        } catch (Exception e) {
            log.error("❌ FastAPI 호출 실패 ({}): {}", email, e.getMessage());
        }
    }
}
