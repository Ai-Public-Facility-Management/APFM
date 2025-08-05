package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.domain.InspectionTimeOption;
import server.dto.InspectionTimeOptionDTO;
import server.repository.InspectionTimeOptionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/inspection-times")
@RequiredArgsConstructor
public class InspectionTimeOptionController {

    private final InspectionTimeOptionRepository repository;

    // 시각 옵션 생성
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody InspectionTimeOptionDTO dto) {
        InspectionTimeOption option = InspectionTimeOption.builder()
                .time(dto.getTime())
                .build();
        repository.save(option);
        return ResponseEntity.ok().build();
    }

    // 시각 옵션 전체 조회
    @GetMapping
    public List<InspectionTimeOption> getAll() {
        return repository.findAll();
    }

    // 시각 옵션 수정
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody InspectionTimeOptionDTO dto) {
        InspectionTimeOption option = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 시각 없음"));
        option.setTime(dto.getTime());
        repository.save(option);
    }

    // 시각 옵션 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

/*
✅ 설명: InspectionTimeOption의 목적

- 점검 시각도 마찬가지로, 직접 입력하지 않고 선택 가능한 시간 목록을 관리
- 프론트 드롭다운에서 제공될 시각 옵션을 미리 정의함
- 유지보수성과 입력 제약 강화를 위해 시간 목록 분리 설계
*/