package server.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "reports", path = "reports")
public interface ReportRepository
    extends PagingAndSortingRepository<Report, Long> {}
