package server.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import server.domain.Users;
import server.repository.UserRepository;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionSchedulerService {

    // 🔹 REST 요청을 보내기 위한 도구 (FastAPI 호출용)
    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ [1] 주기적인 작업 수행: cron 기준 (매시 정각에 실행됨)
    @Scheduled(cron = "0 0 * * * *") // 실제 운영 시: 사용자마다 개별 스케줄을 동적으로 관리
    public void performScheduledInspections() {
        log.info("🕒 주기적 점검 시작");

        // 🔧 테스트용: 사용자 사용자 email 목록 조회, 실제론 DB에서 조회
        List<String> emails = getEmailsToInspect();

        // 🔁 사용자별로 FastAPI 점검 요청
        for (String email : emails) {
            callFastApi(email);
        }
    }

    // ✅ [2] FastAPI에 POST 요청 전송 → 점검 수행 요청
    public void callFastApi(String email) {
        String fastapiUrl = "http://localhost:8000/ai/inspect";  // 🔧 운영 시: config 파일에 주소 분리 추천

        // 요청 바디 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", email);

        // HTTP 헤더 설정 (JSON 포맷)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 최종 요청 객체 구성
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            // POST 요청 → FastAPI에서 점검 수행
            ResponseEntity<String> response = restTemplate.postForEntity(fastapiUrl, entity, String.class);
            log.info("✅ FastAPI 응답 수신(email: {}): {}", email, response.getBody());
            // ✅ 실제 사용 시:
            // ObjectMapper로 response.getBody() → DTO 변환
            // 변환된 DTO를 DB에 저장
            //
            // 예:
            // InspectionResultDTO dto = objectMapper.readValue(response.getBody(), InspectionResultDTO.class);
            // inspectionService.saveInspectionResult(dto);

        } catch (Exception e) {
            log.error("❌ FastAPI 호출 실패: {}", e.getMessage());
        }
    }

    // ✅ [3] 테스트용 사용자 리스트
    private List<String> getEmailsToInspect() {
        // 🔧 조건 추가 가능: 승인된 사용자만, 특정 부서만 등
        List<Users> users = userRepository.findAll();

        return users.stream()
                .map(Users::getEmail)
                .toList();
    }
}
