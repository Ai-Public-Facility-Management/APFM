// [기능 요약] 게시글/댓글 비즈니스 로직 (검색, 조회수, (수정됨) 처리, 소프트삭제)
package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.domain.BoardAttachment;
import server.domain.BoardComment;
import server.domain.BoardPost;
import server.dto.BoardDTO.*;
import server.repository.BoardCommentRepository;
import server.repository.BoardPostRepository;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardPostRepository postRepo;
    private final BoardCommentRepository commentRepo;
    private final SecurityUserResolver userResolver;

    // [기능 요약] 게시글 생성
    @Transactional
    public PostResp create(PostCreateReq req) {
        String email = userResolver.currentUserEmail();

        BoardPost post = BoardPost.builder()
                .type(req.type == null ? BoardPost.PostType.FREE : req.type)
                .title(req.title)
                .content(req.content)
                .isPinned(req.pinned)
                .department(req.department)
                .authorEmail(email)
                .build();

        if (req.attachments != null) {
            for (AttachmentReq a : req.attachments) {
                post.getAttachments().add(BoardAttachment.builder()
                        .post(post)
                        .originalName(a.originalName)
                        .storedUrl(a.storedUrl)
                        .build());
            }
        }

        post = postRepo.save(post);
        long commentCount = 0L;
        return toPostResp(post, commentCount);
    }

    // [기능 요약] 게시글 단건 조회(+조회수 증가, 댓글수 포함)
    @Transactional
    public PostResp getAndIncreaseView(Long id) {
        BoardPost post = postRepo.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        post.setViewCount(post.getViewCount() + 1);
        long commentCount = commentRepo.countActiveByPostId(id);
        return toPostResp(post, commentCount);
    }

    // [변경됨] 게시글 검색/목록
    @Transactional(readOnly = true)
    public Page<PostResp> search(BoardPost.PostType type, String q, Pageable pageable) {
        Page<BoardPost> posts;

        if (q == null || q.isBlank()) {
            // 검색어 없을 때
            if (type == null) {
                // 🔹 type이 없으면 전체 조회
                posts = postRepo.findByDeletedAtIsNull(pageable);
            } else {
                posts = postRepo.findByDeletedAtIsNullAndType(type, pageable);
            }
        } else {
            String keyword = q.trim();
            if (type == null) {
                posts = postRepo.findByDeletedAtIsNullAndTitleContainingIgnoreCaseOrDeletedAtIsNullAndContentContainingIgnoreCase(
                        keyword, keyword, pageable
                );
            } else {
                posts = postRepo.findByDeletedAtIsNullAndTypeAndTitleContainingIgnoreCaseOrDeletedAtIsNullAndTypeAndContentContainingIgnoreCase(
                        type, keyword, type, keyword, pageable
                );
            }
        }

        return posts.map(p -> toPostResp(p, commentRepo.countActiveByPostId(p.getId())));
    }

    // [기능 요약] 게시글 수정(작성자/관리자)
    @Transactional
    public PostResp update(Long id, PostUpdateReq req) {
        BoardPost post = postRepo.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        userResolver.ensureOwnerOrAdmin(post.getAuthorEmail());

        if (req.title != null) post.setTitle(req.title);
        if (req.content != null) post.setContent(req.content);
        if (req.pinned != null) post.setPinned(req.pinned);
        if (req.department != null) post.setDepartment(req.department);

        if (req.attachments != null) {
            post.getAttachments().clear();
            for (AttachmentReq a : req.attachments) {
                post.getAttachments().add(BoardAttachment.builder()
                        .post(post)
                        .originalName(a.originalName)
                        .storedUrl(a.storedUrl)
                        .build());
            }
        }

        long commentCount = commentRepo.countActiveByPostId(id);
        return toPostResp(post, commentCount);
    }

    // [기능 요약] 게시글 삭제(소프트)
    @Transactional
    public void delete(Long id) {
        BoardPost post = postRepo.findActiveById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        userResolver.ensureOwnerOrAdmin(post.getAuthorEmail());
        post.softDelete();
    }

    // [기능 요약] 댓글 목록(페이징)
    @Transactional(readOnly = true)
    public Page<CommentResp> getComments(Long postId, Pageable pageable) {
        return commentRepo.findAllByPostIdAndDeletedAtIsNullOrderByIdAsc(postId, pageable)
                .map(this::toCommentResp);
    }

    // [기능 요약] 댓글 작성
    @Transactional
    public CommentResp addComment(Long postId, CommentCreateReq req) {
        String email = userResolver.currentUserEmail();
        BoardPost post = postRepo.findActiveById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        BoardComment c = commentRepo.save(BoardComment.builder()
                .post(post)
                .authorEmail(email)
                .content(req.getContent())
                .edited(false)
                .build());
        return toCommentResp(c);
    }

    // [기능 요약] 댓글 수정 → (수정됨)
    @Transactional
    public CommentResp updateComment(Long commentId, String newContent) {
        BoardComment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        userResolver.ensureOwnerOrAdmin(c.getAuthorEmail());
        c.setContent(newContent);
        c.setEdited(true);
        return toCommentResp(c);
    }

    // [기능 요약] 댓글 삭제(소프트)
    @Transactional
    public void deleteComment(Long commentId) {
        BoardComment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        userResolver.ensureOwnerOrAdmin(c.getAuthorEmail());
        c.softDelete();
    }

    // ===== mapper =====
    private PostResp toPostResp(BoardPost p, long commentCount) {
        return PostResp.builder()
                .id(p.getId())
                .type(p.getType().name())
                .title(p.getTitle())
                .content(p.getContent())
                .pinned(p.isPinned())
                .viewCount(p.getViewCount())
                .authorEmail(p.getAuthorEmail())
                .department(p.getDepartment())
                .commentCount(commentCount)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .attachments(
                        p.getAttachments().stream()
                                .map(a -> AttachmentResp.builder()
                                        .id(a.getId())
                                        .originalName(a.getOriginalName())
                                        .storedUrl(a.getStoredUrl())
                                        .build()
                                )
                                .collect(Collectors.toList())
                )
                .build();
    }

    private CommentResp toCommentResp(BoardComment c) {
        return CommentResp.builder()
                .id(c.getId())
                .content(c.getContent())
                .authorEmail(c.getAuthorEmail())
                .edited(c.isEdited())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
