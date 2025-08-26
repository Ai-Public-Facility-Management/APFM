// [기능 요약] 게시글/댓글 REST API (분리형, (수정됨) 지원)
package server.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import server.domain.BoardPost;
import server.dto.BoardDTO.*;
import server.service.BoardService;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // ===== 게시글 =====

    // [기능 요약] 게시글 목록/검색
    @GetMapping
    public ResponseEntity<PageResp<PostResp>> list(
            @RequestParam(required = false) BoardPost.PostType type,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        var p = boardService.search(type, q, pageable);
        return ResponseEntity.ok(PageResp.of(p));
    }


    // [기능 요약] 게시글 상세(+조회수 증가)
    @GetMapping("/{postId}")
    public ResponseEntity<PostResp> get(@PathVariable Long postId) {
        return ResponseEntity.ok(boardService.getAndIncreaseView(postId));
    }

    // [기능 요약] 게시글 작성
    @PostMapping
    public ResponseEntity<PostResp> create(
            @RequestPart(required = false) MultipartFile file,
            @RequestPart PostCreateReq req) throws IOException {
        return ResponseEntity.ok(boardService.create(file,req));
    }

    // [기능 요약] 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostResp> update(
            @PathVariable Long postId,
            @RequestPart(required = false) MultipartFile file, // 새로 첨부된 파일
            @RequestPart PostUpdateReq req                     // JSON 본문
    ) throws IOException {
        return ResponseEntity.ok(boardService.update(postId, file, req));
    }

    // [기능 요약] 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId) {
        boardService.delete(postId);
        return ResponseEntity.noContent().build();
    }

    // ===== 댓글 =====

    // [기능 요약] 댓글 목록(페이징)
    @GetMapping("/{postId}/comments")
    public ResponseEntity<PageResp<CommentResp>> comments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sort
    ) {
        Sort s = "oldest".equalsIgnoreCase(sort)
                ? Sort.by(Sort.Direction.ASC, "id")
                : Sort.by(Sort.Direction.DESC, "id");
        var p = boardService.getComments(postId, PageRequest.of(page, size, s));
        return ResponseEntity.ok(PageResp.of(p));
    }

    // [기능 요약] 댓글 작성
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResp> addComment(
            @PathVariable Long postId,
            @RequestBody CommentCreateReq req
    ) {
        return ResponseEntity.ok(boardService.addComment(postId, req));
    }

    // [기능 요약] 댓글 수정 → (수정됨) 처리
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResp> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentUpdateReq req
    ) {
        return ResponseEntity.ok(boardService.updateComment(commentId, req.getContent()));
    }

    // [기능 요약] 댓글 삭제(소프트)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Long commentId) {
        boardService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of("deleted", true));
    }
}
