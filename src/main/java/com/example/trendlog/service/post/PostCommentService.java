package com.example.trendlog.service.post;

import com.example.trendlog.domain.user.User;
import com.example.trendlog.domain.post.Post;
import com.example.trendlog.domain.post.PostComment;
import com.example.trendlog.domain.post.PostCommentLike;
import com.example.trendlog.dto.request.post.PostCommentRequest;
import com.example.trendlog.dto.response.post.PostCommentResponse;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.repository.user.UserRepository;
import com.example.trendlog.repository.post.PostCommentLikeRepository;
import com.example.trendlog.repository.post.PostCommentRepository;
import com.example.trendlog.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;

import static com.example.trendlog.global.exception.code.PostErrorCode.*;
import static com.example.trendlog.global.exception.code.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional
public class PostCommentService {
    private final PostCommentRepository postCommentRepository;
    private final PostCommentLikeRepository postCommentLikeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 게시글 댓글 목록 조회
    public List<PostCommentResponse> getPostCommentList(User user, Long postId) {
        return postCommentRepository.findAllByPostIdAndDeletedFalseOrderByCreatedAt(postId).stream()
                .map(comment -> {
                    boolean isLiked = false;
                    if (user != null) {
                        isLiked = postCommentLikeRepository.existsByUserIdAndPostCommentId(user.getId(), comment.getId());
                    }
                    return PostCommentResponse.from(comment, isLiked);
                })
                .toList();
    }

    // 댓글 추가
    public void addPostComment(Principal principal, Long postId, PostCommentRequest content) {
        User user = findUser(principal);
        Post post = getPost(postId);
        content.validate();
        postCommentRepository.save(PostComment.of(content.content(),user, post));
    }


    // 댓글 삭제(소프트삭제)
    public void deletePostComment(Principal principal, Long postId, Long commentId) {
        User user = findUser(principal);
        getPost(postId);
        PostComment postComment = getPostComment(commentId);
        // 댓글 작성자와 요청한 사용자가 일치하는지 확인
        if (postComment.getUser().getId()!= user.getId()) {
            throw new AppException(POST_NOT_WRITER);
        }
        postComment.deleteComment();
    }

    // 댓글 좋아요
    public void likePostComment(Principal principal, Long postId, Long commentId) {
        User user = findUser(principal);
        Post post = getPost(postId);
        PostComment postComment = getPostComment(commentId);

        if(postCommentLikeRepository.existsByUserIdAndPostCommentId(user.getId(), commentId)) {
            postCommentLikeRepository.deleteByUserIdAndPostCommentId(user.getId(), commentId);
            postComment.changeLikeCount(-1);
        } else {
            postCommentLikeRepository.save(PostCommentLike.of(user, postComment));
            postComment.changeLikeCount(+1);
        }
    }


    private Post getPost(Long postId) {
        return postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));
    }

    private PostComment getPostComment(Long commentId) {
        return postCommentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(POST_COMMENT_NOT_FOUND));
    }

    private User findUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));
    }
}
