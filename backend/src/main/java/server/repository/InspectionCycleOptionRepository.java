package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.InspectionCycleOption;

public interface InspectionCycleOptionRepository extends JpaRepository<InspectionCycleOption, Long> {}