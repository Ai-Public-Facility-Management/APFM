package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import server.domain.Issue;
import server.repository.IssueRepository;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProposalService {

    private final IssueRepository issueRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final WebClient.Builder webClientBuilder;



    /**
     * 선택된 Issue ID들로 DOCX 제안서를 생성
     */
    @Transactional(readOnly = true)
    public ResponseEntity<String> generateProposalForIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
        String fastapiUrl = "http://fastapi:8080/ai/proposal/generate-from-spring";
        // 1. DB 조회
        List<Issue> found = issueRepository.findAllById(ids);

        // 2. FastAPI에 보낼 JSON 변환
        List<Map<String, Object>> estimations = found.stream()
                .map(i -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("vision_analysis", i.getVisionAnalysis());
                    m.put("estimate", i.getEstimate() != null ? i.getEstimate() : 0L);
                    m.put("estimateBasis", i.getEstimateBasis());
                    return m;
                })
                .collect(Collectors.toList());

        found.forEach(i -> {
            i.setProcessing(true);
        });

        Map<String, Object> body = new HashMap<>();
        body.put("estimations", estimations);

        // 4️⃣ HTTP Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 5️⃣ 요청 Entity 생성
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 6️⃣ 요청 전송
        ResponseEntity<Map<String, String>> response =
                restTemplate.exchange(
                        fastapiUrl,
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<>() {
                        }
                );
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
