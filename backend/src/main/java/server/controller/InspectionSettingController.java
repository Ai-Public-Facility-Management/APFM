package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.domain.InspectionSetting;
import server.dto.InspectionSettingRequestDTO;
import server.repository.InspectionSettingRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/inspection-settings")
@RequiredArgsConstructor
public class InspectionSettingController {

    private final InspectionSettingRepository settingRepository;

    // 점검 주기 설정 등록
    @PostMapping
    public ResponseEntity<Void> saveSetting(@RequestBody InspectionSettingRequestDTO dto) {
        InspectionSetting setting = InspectionSetting.builder()
                .startDate(dto.getStartDate())
                .startTime(dto.getStartTime())
                .inspectionCycle(dto.getInspectionCycle())
                // .address(dto.getAddress())
                .build();

        settingRepository.save(setting);
        return ResponseEntity.ok().build();
    }

    // 가장 최근 설정 조회
    // @GetMapping("/latest")
    // public ResponseEntity<InspectionSetting> getLatestSetting() {
    //     Optional<InspectionSetting> setting = settingRepository.findTopByOrderByIdDesc();
    //     return setting.map(ResponseEntity::ok)
    //             .orElse(ResponseEntity.noContent().build());
    // }

    // 기존 설정 수정
    // @PutMapping("/{id}")
    // public ResponseEntity<Void> updateSetting(@PathVariable Long id, @RequestBody InspectionSettingRequestDTO dto) {
    //     InspectionSetting setting = settingRepository.findById(id)
    //             .orElseThrow(() -> new IllegalArgumentException("해당 설정이 없습니다."));

    //     setting.setStartDate(dto.getStartDate());
    //     setting.setStartTime(dto.getStartTime());
    //     setting.setInspectionCycle(dto.getInspectionCycle());
    //     setting.setAddress(dto.getAddress());

    //     settingRepository.save(setting);
    //     return ResponseEntity.ok().build();
    // }
}
