package server.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.Camera;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "cameras", path = "cameras")
public interface CameraRepository
    extends PagingAndSortingRepository<Camera, Long> {}
    
