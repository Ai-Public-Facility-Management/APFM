package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.ResultReport;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "resultReports",
    path = "resultReports"
)
public interface ResultReportRepository
    extends JpaRepository<ResultReport, Integer> {}
