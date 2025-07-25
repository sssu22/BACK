package com.example.trendlog.repository.post;

import com.example.trendlog.domain.post.PostCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostCommentLikeRepository extends JpaRepository<PostCommentLike, Long> {
    boolean existsByUserIdAndPostCommentId(UUID userId, Long postCommentId);
    void deleteByUserIdAndPostCommentId(UUID userId, Long postCommentId);
}
