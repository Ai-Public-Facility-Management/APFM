// [기능 요약] 게시글 목록/검색/단건(소프트삭제 제외)
package server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import server.domain.BoardPost;

import java.util.Optional;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    // [변경됨] JPQL 대신 Spring Data JPA 메서드 쿼리 사용
    Page<BoardPost> findByDeletedAtIsNullAndType(BoardPost.PostType type, Pageable pageable);

    Page<BoardPost> findByDeletedAtIsNullAndTitleContainingIgnoreCaseOrDeletedAtIsNullAndContentContainingIgnoreCase(
            String titleKeyword, String contentKeyword, Pageable pageable
    );

    Page<BoardPost> findByDeletedAtIsNullAndTypeAndTitleContainingIgnoreCaseOrDeletedAtIsNullAndTypeAndContentContainingIgnoreCase(
            BoardPost.PostType type, String titleKeyword,
            BoardPost.PostType type2, String contentKeyword,
            Pageable pageable
    );

    @Query("select p from BoardPost p where p.id = :id and p.deletedAt is null")
    Optional<BoardPost> findActiveById(Long id);
}
