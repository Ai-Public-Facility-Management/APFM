package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.domain.InspectionAddress;
import server.dto.InspectionAddressDTO;
import server.repository.InspectionAddressRepository;

import java.util.List;

@RestController
@RequestMapping("/api/inspection-addresses")
@RequiredArgsConstructor
public class InspectionAddressController {

    private final InspectionAddressRepository repository;

    // 주소 등록
    @PostMapping
    public ResponseEntity<Void> create(@RequestBody InspectionAddressDTO dto) {
        InspectionAddress address = InspectionAddress.builder()
                .locationName(dto.getLocationName())
                .build();
        repository.save(address);
        return ResponseEntity.ok().build();
    }

    // 주소 목록 조회
    @GetMapping
    public List<InspectionAddress> getAll() {
        return repository.findAll();
    }

    // 주소 수정
    @PutMapping("/{id}")
    public void update(@PathVariable Long id, @RequestBody InspectionAddressDTO dto) {
        InspectionAddress address = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 주소 없음"));
        address.setLocationName(dto.getLocationName());
        repository.save(address);
    }

    // 주소 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repository.deleteById(id);
    }
}

/*
 * ✅ 설명: InspectionAddress의 목적

- 점검 대상 시설물이나 위치에 대한 주소를 관리하기 위한 옵션 테이블
- 자주 사용하는 위치를 미리 등록해두고, 드롭다운 등으로 선택하게 구성 가능
- 향후 지도 API 연동이나 시설물 관리와 연동될 수 있음
*/