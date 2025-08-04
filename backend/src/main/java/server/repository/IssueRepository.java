package server.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.Issue;

import java.util.List;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "issues", path = "issues")
public interface IssueRepository
    extends JpaRepository<Issue, Long> {
    List<Issue> findByInspection_IdOrderByIdDesc(Long id);
}
