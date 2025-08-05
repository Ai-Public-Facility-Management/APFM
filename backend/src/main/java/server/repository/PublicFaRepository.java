package server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.FacilityStatus;
import server.domain.PublicFa;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "publicFas", path = "publicFas")
public interface PublicFaRepository
    extends JpaRepository<PublicFa, Long> {
    List<PublicFa> findByStatusAndCameraId(FacilityStatus status, Long cameraId);

    List<PublicFa> findTop10ByStatusOrderByIdDesc(FacilityStatus status);

    Page<PublicFa> findAllByOrderByIdDesc(Pageable pageable);
}
