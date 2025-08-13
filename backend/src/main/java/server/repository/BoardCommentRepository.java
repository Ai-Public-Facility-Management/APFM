// [기능 요약] 댓글 페이징/카운트 (소프트삭제 제외)
package server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import server.domain.BoardComment;

public interface BoardCommentRepository extends JpaRepository<BoardComment, Long> {

    Page<BoardComment> findAllByPostIdAndDeletedAtIsNullOrderByIdAsc(Long postId, Pageable pageable);

    @Query("select count(c) from BoardComment c where c.post.id = :postId and c.deletedAt is null")
    long countActiveByPostId(Long postId);
}
