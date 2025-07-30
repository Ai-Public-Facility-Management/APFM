package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.Issue;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "issues", path = "issues")
public interface IssueRepository
    extends JpaRepository<Issue, Long> {}
