// [기능 요약] 게시글 목록/검색/단건(소프트삭제 제외)
package server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.BoardPost;

import java.util.Optional;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    // 🔹 전체 목록 (type 조건 없이)
    Page<BoardPost> findByDeletedAtIsNull(Pageable pageable);

    // 🔹 type 필터
    Page<BoardPost> findByDeletedAtIsNullAndType(BoardPost.PostType type, Pageable pageable);

    // 🔹 title만 검색
    Page<BoardPost> findByDeletedAtIsNullAndTitleContainingIgnoreCase(String title, Pageable pageable);

    // 🔹 type 포함, title 검색
    Page<BoardPost> findByDeletedAtIsNullAndTypeAndTitleContainingIgnoreCase(BoardPost.PostType type, String title, Pageable pageable);

    // 🔹 단건 조회 (soft delete 제외)
    Optional<BoardPost> findByIdAndDeletedAtIsNull(Long id);
}
