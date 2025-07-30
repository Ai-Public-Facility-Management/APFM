package server.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.Report;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "reports", path = "reports")
public interface ReportRepository
    extends PagingAndSortingRepository<Report, Long> {}
