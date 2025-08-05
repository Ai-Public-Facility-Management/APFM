package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;

import server.service.ProposalService;
import java.util.Map;
import java.util.List;

import server.dto.ProposalRequestDTO;
import server.dto.ProposalResponseDTO;

@RestController
@RequestMapping("/api/proposals")  // prefix 유지
@RequiredArgsConstructor
@Transactional
public class ProposalController {

    private final ProposalService proposalService;

    @PostMapping("/generate")
    public ResponseEntity<ProposalDTO> generateProposals(@RequestBody ProposalDTO dto) {
        proposalService.handleProposalGeneration(dto.getIds(), dto.getFileUrl());
        return ResponseEntity.ok(dto);  // 요청으로 받은 DTO를 그대로 응답
    }
}