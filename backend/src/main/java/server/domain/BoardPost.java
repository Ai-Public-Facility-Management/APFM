// [기능 요약] 게시글: 공지/자유/질문, 상단 고정, 조회수, 첨부/댓글(양방향)
package server.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board_post", indexes = {
        @Index(name = "idx_board_post_type", columnList = "type"),
        @Index(name = "idx_board_post_pinned", columnList = "isPinned"),
        @Index(name = "idx_board_post_deleted", columnList = "deletedAt")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardPost extends BaseTimeEntity {

    public enum PostType { NOTICE, FREE, QUESTION }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PostType type;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    /**
     * 작성자(User) 매핑
     * Users의 PK(email)과 FK(user_email)로 연결
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_email", referencedColumnName = "email", nullable = false)
    private Users author;

    @Column(length = 100)
    private String department;

    @Comment("상단 고정(공지)")
    private boolean isPinned;

    @Comment("조회수")
    private long viewCount;

    // 첨부/댓글: orphanRemoval=true로 교체/삭제 편의
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BoardAttachment> attachments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    @Builder.Default
    private List<BoardComment> comments = new ArrayList<>();
}
