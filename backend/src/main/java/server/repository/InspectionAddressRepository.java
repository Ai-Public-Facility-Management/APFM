package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.InspectionAddress;

public interface InspectionAddressRepository extends JpaRepository<InspectionAddress, Long> {}
