package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.Proposal;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "proposals", path = "proposals")
public interface ProposalRepository
    extends JpaRepository<Proposal, Integer> {}
