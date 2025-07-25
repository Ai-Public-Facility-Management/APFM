package untitled.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import untitled.domain.*;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "proposals", path = "proposals")
public interface ProposalRepository
    extends PagingAndSortingRepository<Proposal, Integer> {}
