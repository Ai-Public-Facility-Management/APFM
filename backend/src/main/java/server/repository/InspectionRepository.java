package server.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.Inspection;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "inspections",
    path = "inspections"
)
public interface InspectionRepository
    extends PagingAndSortingRepository<Inspection, Long> {}
