// [기능 요약] 댓글: 작성자/내용/소프트삭제 + (수정됨) 표시
package server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_comment", indexes = {
        @Index(name = "idx_board_comment_post", columnList = "post_id"),
        @Index(name = "idx_board_comment_deleted", columnList = "deletedAt")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardComment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private BoardPost post;

    @Column(nullable = false, length = 120)
    private String authorEmail;

    @Lob
    @Column(nullable = false)
    private String content;

    // (수정됨) 렌더링을 위한 플래그 (createdAt vs updatedAt 비교만으로도 가능하나 UX 단순화를 위해 유지)
    @Column(nullable = false)
    @Builder.Default
    private boolean edited = false;
}
