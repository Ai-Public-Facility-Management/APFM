package server.domain;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "cameras", path = "cameras")
public interface CameraRepository
    extends PagingAndSortingRepository<Camera, Long> {}
