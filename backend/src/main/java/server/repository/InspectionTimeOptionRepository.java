package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.InspectionTimeOption;

public interface InspectionTimeOptionRepository extends JpaRepository<InspectionTimeOption, Long> {}