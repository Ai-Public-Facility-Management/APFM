package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import server.domain.InspectionSetting;



@Repository
public interface InspectionSettingRepository extends JpaRepository<InspectionSetting, Long> {
    // 별도 쿼리 없이 전체 조회 후 서비스 단에서 계산
}
