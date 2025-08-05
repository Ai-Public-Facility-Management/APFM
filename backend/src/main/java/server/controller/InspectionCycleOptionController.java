package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.domain.InspectionCycleOption;
import server.dto.InspectionCycleOptionDTO;
import server.repository.InspectionCycleOptionRepository;

import java.util.List;

@RestController
@RequestMapping("/api/inspection-cycles")
@RequiredArgsConstructor
public class InspectionCycleOptionController {

    private final InspectionCycleOptionRepository repository;

    // 주기 옵션 생성
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody InspectionCycleOptionDTO dto) {
        InspectionCycleOption option = InspectionCycleOption.builder()
                .cycleDay(dto.getCycleDay())
                .build();
        repository.save(option);
        return ResponseEntity.ok().build();
    }

    // 주기 옵션 전체 조회
    @GetMapping
    public List<InspectionCycleOption> getAll() {
        return repository.findAll();
    }

    // 주기 옵션 수정
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody InspectionCycleOptionDTO dto) {
        InspectionCycleOption option = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 주기 없음"));
        option.setCycleDay(dto.getCycleDay());
        repository.save(option);
    }

    // 주기 옵션 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}


/*
✅ 설명: InspectionCycleOption의 목적

- 점검 주기를 사용자에게 직접 입력받는 대신, 미리 정의된 옵션 목록 중에서 선택하도록 하기 위함
- 프론트에서는 이 목록을 드롭다운으로 불러와서 선택 가능
- 관리자 또는 운영자가 옵션을 등록/수정/삭제할 수 있음
*/