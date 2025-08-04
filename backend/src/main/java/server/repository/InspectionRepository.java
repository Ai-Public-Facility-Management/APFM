package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.*;

import java.util.List;

//<<< PoEAA / Repository
@RepositoryRestResource(
    collectionResourceRel = "inspections",
    path = "inspections"
)
public interface InspectionRepository extends JpaRepository<Inspection, Long> {
    public List<Inspection> findTop5ByIsInspectedTrueOrderByCreateDateDesc();
}
