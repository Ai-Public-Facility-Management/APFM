package server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import server.domain.*;
import server.dto.BoardDTO.*;
import server.repository.BoardCommentRepository;
import server.repository.BoardPostRepository;
import server.repository.UsersRepository;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardPostRepository postRepo;
    private final BoardCommentRepository commentRepo;
    private final UsersRepository usersRepo;
    private final SecurityUserResolver userResolver;
    private final AzureService azureService;
    // [기능 요약] 게시글 생성
    @Transactional
    public PostResp create(MultipartFile file,PostCreateReq req) throws IOException {
        String email = userResolver.currentUserEmail();
        Users author = usersRepo.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        String url = null;
        if(file != null){
            url = azureService.azureBlobUpload(file,".png");
        }
        BoardPost post = BoardPost.builder()
                .type(req.type == null ? BoardPost.PostType.FREE : req.type)
                .title(req.title)
                .content(req.content)
                .imageUrl(url) // 이미지 주소 저장
                .author(author) // Users 매핑
                .build();

        post = postRepo.save(post);
        boolean isAuthor = true;
        return toPostResp(post, 0L, isAuthor);
    }

    // [기능 요약] 게시글 단건 조회(+조회수 증가, 댓글수 포함)
    @Transactional
    public PostResp getAndIncreaseView(Long postId) {
        BoardPost post = postRepo.findByIdAndDeletedAtIsNull(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.setViewCount(post.getViewCount() + 1);
        long commentCount = commentRepo.countActiveByPostId(postId);
        String currentUserEmail = null;
        try {
            currentUserEmail = userResolver.currentUserEmail();
        } catch (Exception ignored) {
            // 비로그인 사용자는 null
        }

        boolean isAuthor = currentUserEmail != null &&
                currentUserEmail.equals(post.getAuthor().getEmail());

        return toPostResp(post, commentCount, isAuthor);
    }

    // [변경됨] 게시글 검색/목록
    @Transactional(readOnly = true)
    public Page<PostResp> search(BoardPost.PostType type, String q, Pageable pageable) {
        Page<BoardPost> posts;

        if (q == null || q.isBlank()) {
            if (type == null) {
                posts = postRepo.findByDeletedAtIsNull(pageable);
            } else {
                posts = postRepo.findByDeletedAtIsNullAndType(type, pageable);
            }
        } else {
            String keyword = q.trim();
            if (type == null) {
                posts = postRepo.findByDeletedAtIsNullAndTitleContainingIgnoreCase(keyword, pageable);
            } else {
                posts = postRepo.findByDeletedAtIsNullAndTypeAndTitleContainingIgnoreCase(type, keyword, pageable);
            }
        }

        return posts.map(p -> toPostResp(p, commentRepo.countActiveByPostId(p.getId()), false));
    }

    // [기능 요약] 게시글 수정(작성자/관리자)
    @Transactional
    public PostResp update(Long postId, MultipartFile file, PostUpdateReq req) throws IOException {
        BoardPost post = postRepo.findByIdAndDeletedAtIsNull(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        userResolver.ensureOwnerOrAdmin(post.getAuthor().getEmail());

        if (req.title != null) post.setTitle(req.title);
        if (req.content != null) post.setContent(req.content);

        // 파일이 있으면 Azure에 업로드 후 이미지 URL 교체
        if (file != null && !file.isEmpty()) {
            String url = azureService.azureBlobUpload(file, ".png");
            post.setImageUrl(url);
        }

        String currentUserEmail = userResolver.currentUserEmail();
        boolean isAuthor = currentUserEmail.equals(post.getAuthor().getEmail());
        long commentCount = commentRepo.countActiveByPostId(postId);

        return toPostResp(post, commentCount, isAuthor);
    }

    // [기능 요약] 게시글 삭제(소프트)
    @Transactional
    public void delete(Long postId) {
        BoardPost post = postRepo.findByIdAndDeletedAtIsNull(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        userResolver.ensureOwnerOrAdmin(post.getAuthor().getEmail());
        post.softDelete(userResolver.currentUser());
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
        Users author = usersRepo.findById(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        BoardPost post = postRepo.findByIdAndDeletedAtIsNull(postId)
            .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        BoardComment c = commentRepo.save(BoardComment.builder()
                .post(post)
                .author(author)
                .content(req.getContent())
                .edited(false)
                .build());
        return toCommentResp(c);
    }

    // [기능 요약] 댓글 수정
    @Transactional
    public CommentResp updateComment(Long commentId, String newContent) {
        BoardComment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        userResolver.ensureOwnerOrAdmin(c.getAuthor().getEmail());
        c.setContent(newContent);
        c.setEdited(true);
        return toCommentResp(c);
    }

    // [기능 요약] 댓글 삭제(소프트)
    @Transactional
    public void deleteComment(Long commentId) {
        BoardComment c = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        userResolver.ensureOwnerOrAdmin(c.getAuthor().getEmail());
        c.softDelete(userResolver.currentUser());
    }

    // ===== mapper =====
    private PostResp toPostResp(BoardPost p, long commentCount, boolean isAuthor) {
        String sasUrl = null;
        if(p.getImageUrl() != null){
            sasUrl = azureService.azureBlobSas(p.getImageUrl());
        }

        return PostResp.builder()
                .id(p.getId())
                .type(p.getType().name())
                .title(p.getTitle())
                .content(p.getContent())
                .imageUrl(sasUrl) // 이미지 주소 내려줌
                .viewCount(p.getViewCount())
                .authorEmail(p.getAuthor().getEmail())
                .authorName(p.getAuthor().getUsername())
                .authorDepartment(p.getAuthor().getDepartment().name())
                .commentCount(commentCount)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .isAuthor(isAuthor)
                .build();
    }

    private CommentResp toCommentResp(BoardComment c) {
        String currentUserEmail = null;
        try {
            currentUserEmail = userResolver.currentUserEmail();
        } catch (Exception ignored) {
            // 비로그인 시 null
        }

        boolean isAuthor = currentUserEmail != null &&
                currentUserEmail.equals(c.getAuthor().getEmail());

        return CommentResp.builder()
                .id(c.getId())
                .content(c.getContent())
                .authorName(c.getAuthor().getUsername())
                .edited(c.isEdited())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .isAuthor(isAuthor)
                .build();
    }
}
