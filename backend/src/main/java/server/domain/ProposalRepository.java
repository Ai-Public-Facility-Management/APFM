package server.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "proposals", path = "proposals")
public interface ProposalRepository
    extends PagingAndSortingRepository<Proposal, Integer> {}
