package server.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import server.domain.*;

import java.util.List;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Long> {

    // ✅ 점검 리스트 조회 시 연관 이슈 및 리포트까지 로딩
    @EntityGraph(attributePaths = {"issues", "report"})
    Page<Inspection> findAll(Pageable pageable);

    // ✅ 메인 대시보드용 최근 점검 리스트 (유동적 count 사용)
    List<Inspection> findByIsinspectedTrueOrderByCreateDateDesc(Pageable pageable);
}

