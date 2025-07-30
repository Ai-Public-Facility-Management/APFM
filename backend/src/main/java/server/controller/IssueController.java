package server.controller;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import server.repository.IssueRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import server.repository.*;
import server.domain.*;
import server.dto.*;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping("/api/issues")
@Transactional
public class IssueController {

    @Autowired
    IssueRepository issueRepository;

    @Autowired  // ✅ 꼭 필요!
    ProposalRepository proposalRepository;

    @PostMapping
    public Issue createIssue(@RequestBody Issue issue) {
        return issueRepository.save(issue);
    }

    @PostMapping("/{id}/generate-proposal")
    public ResponseEntity<String> generateProposal(@PathVariable Long id) {
        Issue issue = issueRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 이슈를 찾을 수 없습니다: " + id));

        // estimate(String)을 숫자로 파싱
        String estimateStr = issue.getEstimate().replaceAll("[^0-9]", "");
        int estimateInt = estimateStr.isEmpty() ? 0 : Integer.parseInt(estimateStr);

        // content 채우기 (Photo 타입 사용)
        // Photo photo = new Photo();
        // photo.setImageDescription("이슈 기반 자동 제안서 생성");
        // photo.setImageUrl("https://example.com/generated-proposal.jpg");  // 또는 고정 이미지

        // Proposal 객체 생성
        Proposal proposal = new Proposal();
        proposal.setTotalEstimate(estimateInt);
        // proposal.setContent(photo);
        proposal.setIssue(issue);  // 연관된 이슈 설정

        // 저장
        proposalRepository.save(proposal);

        return ResponseEntity.ok("Proposal 생성 완료: Issue ID = " + id);
    }
}

//>>> Clean Arch / Inbound Adaptor
