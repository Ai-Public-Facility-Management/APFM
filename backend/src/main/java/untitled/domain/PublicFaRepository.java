package untitled.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import untitled.domain.*;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "publicFas", path = "publicFas")
public interface PublicFaRepository
    extends PagingAndSortingRepository<PublicFa, Long> {}
