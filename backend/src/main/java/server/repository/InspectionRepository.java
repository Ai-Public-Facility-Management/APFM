package server.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import server.domain.*;

import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    // 상세 조회 시 이슈/연관 데이터까지 한 번에 조회 (N+1 방지)
    @EntityGraph(attributePaths = {
            "issues",
            "issues.publicFa",
            "issues.publicFa.camera"
            // 필요 시 "report" 등 추가
    })
    Optional<Inspection> findWithIssuesById(Long id);

    // ✅ 점검 리스트 조회 시 연관 이슈 및 리포트까지 로딩
    @EntityGraph(attributePaths = {"issues", "report"})
    Page<Inspection> findAll(Pageable pageable);

    // ✅ 메인 대시보드용 최근 점검 리스트 (유동적 count 사용)
    List<Inspection> findByOrderByCreateDateDesc(Pageable pageable);
}

