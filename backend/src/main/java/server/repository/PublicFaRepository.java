package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.PublicFa;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "publicFas", path = "publicFas")
public interface PublicFaRepository
    extends JpaRepository<PublicFa, Long> {}
