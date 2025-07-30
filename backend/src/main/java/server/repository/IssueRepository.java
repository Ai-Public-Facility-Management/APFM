package server.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "issues", path = "issues")
public interface IssueRepository
    extends PagingAndSortingRepository<Issue, Long> {}
