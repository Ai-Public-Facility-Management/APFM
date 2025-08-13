// [기능 요약] 첨부파일 메타(원본명/저장URL) - 실제 파일은 S3/파일서버에 저장
package server.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board_attachment", indexes = {
        @Index(name = "idx_board_attachment_post", columnList = "post_id")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BoardAttachment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private BoardPost post;

    @Column(nullable = false, length = 255)
    private String originalName;

    @Column(nullable = false, length = 500)
    private String storedUrl;
}
