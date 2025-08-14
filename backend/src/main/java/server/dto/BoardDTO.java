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
    }

    @Getter @Setter
    public static class PostUpdateReq {
        public String title;
        public String content;
        public String imageUrl;
    }

    @Getter @Builder
    public static class PostResp {
        public Long id;
        public String type;
        public String title;
        public String content;
        public String imageUrl; // 이미지 주소
        public long viewCount;
        public String authorEmail;   // Users.email
        public String authorName;    // Users.username
        public String authorDepartment;   // Users.department (enum -> name)
        public long commentCount;    // 목록/상세에서 뱃지용
        public Instant createdAt;
        public Instant updatedAt;
        public boolean isAuthor;
    }

    // ====== Comment ======
    @Getter @Setter
    public static class CommentCreateReq {
        public String content;
    }

    @Getter @Setter
    public static class CommentUpdateReq {
        public String content;
    }

    @Getter @Builder
    public static class CommentResp {
        public Long id;
        public String content;
        public String authorName;    // Users.username
        public boolean edited;
        public Instant createdAt;
        public Instant updatedAt;
        public boolean isAuthor;
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
