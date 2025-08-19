// 📄 server.service.InspectionSchedulerService.java

package server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import server.domain.Camera;
import server.domain.InspectionSetting;
import server.dto.InspectionResultDTO;
import server.repository.CameraRepository;
import server.repository.InspectionSettingRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionSchedulerService {

    private final InspectionSettingRepository inspectionSettingRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final InspectionService inspectionService;
    private final CameraRepository  cameraRepository;

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
            String fastapiUrl = "http://localhost:8000/predict";

//            // 2️⃣ 이미지 파일 준비 (나중에 CCTV 캡처 이미지로 교체)
//            File imageFile = new File("AI/testimg/sample_image.png"); // 실제 경로로 수정 필요
//            FileSystemResource fileResource = new FileSystemResource(imageFile);

            // 3️⃣ Multipart Form 구성
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("camera_ids", camera_ids);

            // 4️⃣ HTTP Header 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 5️⃣ 요청 Entity 생성
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    
            // 6️⃣ 요청 전송
            ResponseEntity<String> response = restTemplate.postForEntity(fastapiUrl, requestEntity, String.class);
            log.info("✅ FastAPI 응답 수신 : {}", response.getBody());

            // 7️⃣ 결과 DTO로 파싱 후 저장
            ObjectMapper objectMapper = new ObjectMapper();
            List<InspectionResultDTO> dtos = Collections.singletonList(objectMapper.readValue(response.getBody(), InspectionResultDTO.class));
            inspectionService.saveInspectionResult(dtos);
    
        } catch (Exception e) {
            log.error("❌ FastAPI 호출 실패 : {}",e.getMessage());
    }
}
}
