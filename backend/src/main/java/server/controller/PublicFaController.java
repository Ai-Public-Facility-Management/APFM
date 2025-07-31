package server.controller;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import server.repository.*;
import server.domain.*;
import server.dto.*;
//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="/publicFas")
@Transactional
@RequiredArgsConstructor
public class PublicFaController {

    private final PublicFaRepository publicFaRepository;

    @GetMapping("/unmatched")
    public ResponseEntity<List<UnmatchedFacilityDTO>> getUnmatchedFacilities(@RequestParam Long inspectionId) {
        List<PublicFa> unmatchedList = publicFaRepository.findByInspection_IdAndMatchedFalse(inspectionId);
    
        List<UnmatchedFacilityDTO> dtos = unmatchedList.stream()
            .map(fa -> new UnmatchedFacilityDTO(fa.getId(), fa.getCategory(), fa.getImageUrl()))  //  fa.getLocation() 필요시 추가
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PatchMapping("/{id}/match-to-db")
    public ResponseEntity<Void> matchToDbFacility(
        @PathVariable Long id,
        @RequestBody Map<String, Long> requestBody) {

        Long matchedId = requestBody.get("matchedPublicFaId");

        PublicFa target = publicFaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("탐지된 시설물이 존재하지 않습니다."));

        publicFaRepository.findById(matchedId)
            .orElseThrow(() -> new IllegalArgumentException("기준 시설물이 존재하지 않습니다."));

        target.setMatched(true);
        target.setMatchedPublicFaId(matchedId);  // 연관관계 매핑
        publicFaRepository.save(target);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePublicFa(@PathVariable Long id) {
        if (!publicFaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        publicFaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
//>>> Clean Arch / Inbound Adaptor
