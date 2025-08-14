package com.example.trendlog.controller;

import com.example.trendlog.dto.request.post.PostCommentRequest;
import com.example.trendlog.dto.request.post.PostCreateUpdateRequest;
import com.example.trendlog.dto.response.post.PostPagedResponse;
import com.example.trendlog.dto.response.post.PostPopularPagedResponse;
import com.example.trendlog.dto.response.post.PostResponse;
import com.example.trendlog.global.docs.PostSwaggerSpec;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.service.post.PostCommentService;
import com.example.trendlog.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController implements PostSwaggerSpec {

    private final PostService postService;
    private final PostCommentService postCommentService;

    // 게시글 생성
    @PostMapping
    public ResponseEntity<DataResponse<Void>> createPost(Principal principal,
                                                         @RequestBody PostCreateUpdateRequest request) {
        postService.createPost(principal, request);
        return ResponseEntity.ok(DataResponse.ok());
    }

    // 게시글 목록 조회 (정렬 + 감정 필터 + 페이징)
    @GetMapping
    public ResponseEntity<DataResponse<PostPagedResponse>> getPostList(
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "all") String emotion,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(DataResponse.from(postService.getPostList(sort, emotion, page, size)));
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<DataResponse<PostResponse>> getPostDetail(Principal principal,
                                                      @PathVariable Long postId) {
        return ResponseEntity.ok(DataResponse.from(postService.getPostDetail(principal, postId)));
    }

    // 인기 게시글 목록 조회
    @GetMapping("/popular")
    public ResponseEntity<DataResponse<PostPopularPagedResponse>> getPopularPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(DataResponse.from(postService.getPopularPostList(page, size)));
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<DataResponse<Void>> updatePost(Principal principal,
                                           @PathVariable Long postId,
                                           @RequestBody PostCreateUpdateRequest request) {
        postService.updatePost(principal, postId, request);
        return ResponseEntity.ok(DataResponse.ok());
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<DataResponse<Void>> deletePost(Principal principal,
                                           @PathVariable Long postId) {
        postService.deletePost(principal, postId);
        return ResponseEntity.ok(DataResponse.ok());
    }

    // 게시글 좋아요
    @PostMapping("/{postId}/like")
    public ResponseEntity<DataResponse<Void>> likePost(Principal principal,
                                         @PathVariable Long postId) {
        postService.likePost(principal, postId);
        return ResponseEntity.ok(DataResponse.ok());
    }

    // 게시글 스크랩
    @PostMapping("/{postId}/scrap")
    public ResponseEntity<DataResponse<Void>> scrapPost(Principal principal,
                                          @PathVariable Long postId) {
        postService.scrapPost(principal, postId);
        return ResponseEntity.ok(DataResponse.ok());
    }

    // 댓글 작성
    @PostMapping("/{postId}/comments")
    public ResponseEntity<DataResponse<Void>> addComment(
            Principal principal,
            @PathVariable Long postId,
            @RequestBody PostCommentRequest content
    ) {
        postCommentService.addPostComment(principal, postId, content);
        return ResponseEntity.ok(DataResponse.ok());
    }

    // 댓글 삭제
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<DataResponse<Void>> deleteComment(
            Principal principal,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        postCommentService.deletePostComment(principal, postId, commentId);
        return ResponseEntity.ok(DataResponse.ok());
    }

    // 댓글 좋아요
    @PostMapping("/{postId}/comments/{commentId}/like")
    public ResponseEntity<DataResponse<Void>> likeComment(
            Principal principal,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        postCommentService.likePostComment(principal, postId, commentId);
        return ResponseEntity.ok(DataResponse.ok());
    }

}

