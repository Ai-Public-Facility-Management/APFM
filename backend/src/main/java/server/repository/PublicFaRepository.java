package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.PublicFa;
import java.util.List;

//<<< PoEAA / Repository
@RepositoryRestResource(collectionResourceRel = "publicFas", path = "publicFas")
public interface PublicFaRepository
    extends JpaRepository<PublicFa, Long> {
         boolean existsByInspection_IdAndMatchedFalse(Long inspectionId);   // 해당 점검 ID에 대해 아직 매칭되지 않은 시설물이 있는지 여부 확인
    }
