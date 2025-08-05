package server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import server.domain.Issue;
import server.domain.IssueType;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "issues", path = "issues")
public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByInspection_IdOrderByIdDesc(Long id);
    // ✅ 점검에서 이슈 개수 (REPAIR, REMOVE 필터링용)
    int countByInspectionIdAndType(Long inspectionId, IssueType type);

    // ✅ 점검 상세 또는 대시보드에서 점검 ID로 이슈 조회
    List<Issue> findByInspection_Id(Long inspectionId);

    // ✅ 대시보드 시설물 목록용: 시설물별 이슈 조회
    List<Issue> findByPublicFaId(Long publicFaId);  // 또는 findByPublicFa_Id(...) (연관관계 방식에 따라)
}

