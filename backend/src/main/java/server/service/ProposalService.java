package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private final WebClient.Builder webClientBuilder;

    @Value("${app.fastapi.base-url}")
    private String fastApiBaseUrl;

    /**
     * 선택된 Issue ID들로 DOCX 제안서를 생성
     */
    @Transactional(readOnly = true)
    public Mono<String> generateProposalForIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Mono.just("");

        }

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


        // 3. FastAPI 호출
        return webClientBuilder.build()
                .post()
                .uri(fastApiBaseUrl + "/proposal/generate-from-spring")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("estimations", estimations))
                .retrieve()
                .bodyToMono(String.class);
    }
}
