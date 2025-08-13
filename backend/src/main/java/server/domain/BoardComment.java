// [기능 요약] 댓글: 작성자/내용/소프트삭제 + (수정됨) 표시
package server.domain;

import jakarta.persistence.*;
import lombok.*;

import server.domain.Users;

@Entity
@Table(name = "board_comment", indexes = {
        @Index(name = "idx_board_comment_post", columnList = "post_id"),
        @Index(name = "idx_board_comment_deleted", columnList = "deletedAt")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardComment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ 댓글이 속한 게시글
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private BoardPost post;

    // ✅ 작성자 정보 (Users 매핑)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Users author;

    @Lob
    @Column(nullable = false)
    private String content;

    // (수정됨) 렌더링을 위한 플래그 (createdAt vs updatedAt 비교만으로도 가능하나 UX 단순화를 위해 유지)
    @Column(nullable = false)
    @Builder.Default
    private boolean edited = false;

    // ✅ 작성자 이름 반환
    public String getAuthorName() {
        return author.getUsername();
    }

    // ✅ 작성자 이메일 반환
    public String getAuthorEmail() {
        return author.getEmail();
    }
}
