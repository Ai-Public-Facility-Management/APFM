package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.InspectionSetting;

import java.util.Optional;

public interface InspectionSettingRepository extends JpaRepository<InspectionSetting, Long> {
    Optional<InspectionSetting> findTopByOrderByIdDesc();
}
