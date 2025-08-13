// [기능 요약] 게시판/댓글 요청·응답 DTO (프론트 타입과 1:1 매핑 가독성↑)
package server.dto;

import lombok.*;
import org.springframework.data.domain.Page;
import server.domain.BoardPost;

import java.time.Instant;
import java.util.List;

public class BoardDTO {

    // ====== Post ======
    @Getter @Setter
    public static class PostCreateReq {
        public BoardPost.PostType type;
        public String title;
        public String content;
        public boolean pinned;
        public String department; // optional
        public List<AttachmentReq> attachments;
    }

    @Getter @Setter
    public static class PostUpdateReq {
        public String title;
        public String content;
        public Boolean pinned;
        public String department; // optional
        public List<AttachmentReq> attachments; // 전체 교체
    }

    @Getter @AllArgsConstructor @NoArgsConstructor
    public static class AttachmentReq {
        public String originalName;
        public String storedUrl;
    }

    @Getter @Builder
    public static class AttachmentResp {
        public Long id;
        public String originalName;
        public String storedUrl;
    }

    @Getter @Builder
    public static class PostResp {
        public Long id;
        public String type;
        public String title;
        public String content;
        public boolean pinned;
        public long viewCount;
        public String authorEmail;
        public String department;
        public long commentCount;     // ← 목록/상세에서 뱃지용으로 권장
        public Instant createdAt;
        public Instant updatedAt;
        public List<AttachmentResp> attachments;
    }

    // ====== Comment ======
    @Getter @Setter
    public static class CommentCreateReq { public String content; }

    @Getter @Setter
    public static class CommentUpdateReq { public String content; }

    @Getter @Builder
    public static class CommentResp {
        public Long id;
        public String content;
        public String authorEmail;
        public boolean edited;
        public Instant createdAt;
        public Instant updatedAt;
    }

    // ====== Page ======
    @Getter @Builder
    public static class PageResp<T> {
        public List<T> content;
        public int page;
        public int size;
        public long totalElements;
        public int totalPages;

        public static <T> PageResp<T> of(Page<T> p) {
            return PageResp.<T>builder()
                    .content(p.getContent())
                    .page(p.getNumber())
                    .size(p.getSize())
                    .totalElements(p.getTotalElements())
                    .totalPages(p.getTotalPages())
                    .build();
        }
    }
}
