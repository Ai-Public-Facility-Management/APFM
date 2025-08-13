// [기능 요약] 게시글 목록/검색/단건(소프트삭제 제외)
package server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import server.domain.BoardPost;

import java.util.Optional;

public interface BoardPostRepository extends JpaRepository<BoardPost, Long> {

    @Query("""
           select p from BoardPost p
           where p.deletedAt is null
             and (:type is null or p.type = :type)
             and (:q is null or (lower(p.title) like lower(concat('%', :q, '%'))
                              or  lower(p.content) like lower(concat('%', :q, '%'))))
           order by p.isPinned desc, p.id desc
           """)
    Page<BoardPost> search(BoardPost.PostType type, String q, Pageable pageable);

    @Query("select p from BoardPost p where p.id = :id and p.deletedAt is null")
    Optional<BoardPost> findActiveById(Long id);
}
