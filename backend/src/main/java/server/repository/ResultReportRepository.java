package server.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "resultReports",
    path = "resultReports"
)
public interface ResultReportRepository
    extends PagingAndSortingRepository<ResultReport, Integer> {}
