package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.Camera;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "cameras", path = "cameras")
public interface CameraRepository
    extends JpaRepository<Camera,Long> {}
    
