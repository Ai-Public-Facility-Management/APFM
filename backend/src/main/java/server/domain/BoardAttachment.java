// [기능 요약] 첨부파일 메타(원본명/저장URL) - 실제 파일은 S3/파일서버에 저장
package server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_attachment", indexes = {
        @Index(name = "idx_board_attachment_post", columnList = "post_id"),
        @Index(name = "idx_board_attachment_author", columnList = "author_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardAttachment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 게시글과의 관계
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private BoardPost post;

    // 첨부파일 등록한 작성자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Users author;

    @Column(nullable = false, length = 255)
    private String originalName;

    @Column(nullable = false, length = 500)
    private String storedUrl;
}
