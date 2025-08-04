// package server.service;

// import lombok.RequiredArgsConstructor;
// import org.springframework.stereotype.Service;
// import server.domain.InspectionSetting;
// import server.dto.InspectionSettingRequestDTO;
// import server.repository.InspectionSettingRepository;

// @Service
// @RequiredArgsConstructor
// public class InspectionSettingService {

//     private final InspectionSettingRepository settingRepository;

//     // 점검 주기 설정 저장
//     public void saveSetting(InspectionSettingRequestDTO dto) {
//         InspectionSetting setting = InspectionSetting.builder()
//                 .startDate(dto.getStartDate())
//                 .startTime(dto.getStartTime())
//                 .inspectionCycle(dto.getInspectionCycle())
//                 .address(dto.getAddress())
//                 .build();

//         settingRepository.save(setting);
//     }
// }

/*
✅ 설명: InspectionSettingService를 만드는 이유

- 현재는 단순 저장 기능만 있지만,
- 나중에 아래와 같은 작업을 추가하기 쉬운 구조로 만들어 둔 것임:
    - 유효성 검증 (예: 시작일이 과거인지 체크)
    - 점검 주기 기준으로 일정 자동 생성
    - 특정 시설물과 연동하여 점검 설정
    - 트랜잭션 처리
    - 테스트 및 재사용 용이성

→ Controller는 단순히 HTTP 요청을 받고 Service에 위임하는 역할만 하도록 분리

- InspectionSettingService를 쓴다고 하면 InspectionSettingController의 //점검 주기 설정 등록 코드를
// 점검 주기 설정 등록
    @PostMapping
    public ResponseEntity<Void> saveSetting(@RequestBody InspectionSettingRequestDTO dto) {
        settingService.saveSetting(dto);
        return ResponseEntity.ok().build();
    }

    이렇게 수정
*/