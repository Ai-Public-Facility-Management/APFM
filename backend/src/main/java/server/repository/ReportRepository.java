package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.Report;
import java.util.Optional;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "reports", path = "reports")
public interface ReportRepository
    extends JpaRepository<Report, Long> {
    Optional<Report> findByInspection_Id(Long inspectionId);
}
