// [기능 요약] 게시글 목록/검색/단건(소프트삭제 제외)
package server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import server.domain.BoardPost;

import java.util.Optional;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    // 🔹 전체 목록 (type 조건 없이)
    Page<BoardPost> findByDeletedAtIsNull(Pageable pageable);

    // 🔹 type만 필터
    Page<BoardPost> findByDeletedAtIsNullAndType(BoardPost.PostType type, Pageable pageable);

    // 🔹 type 없이 제목·내용 검색
    Page<BoardPost> findByDeletedAtIsNullAndTitleContainingIgnoreCaseOrDeletedAtIsNullAndContentContainingIgnoreCase(
            String titleKeyword, String contentKeyword, Pageable pageable
    );

    // 🔹 type 포함 제목·내용 검색
    Page<BoardPost> findByDeletedAtIsNullAndTypeAndTitleContainingIgnoreCaseOrDeletedAtIsNullAndTypeAndContentContainingIgnoreCase(
            BoardPost.PostType type, String titleKeyword,
            BoardPost.PostType type2, String contentKeyword,
            Pageable pageable
    );

    @Query("select p from BoardPost p where p.id = :id and p.deletedAt is null")
    Optional<BoardPost> findActiveById(Long id);
}
