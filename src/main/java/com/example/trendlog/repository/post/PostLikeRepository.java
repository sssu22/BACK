package com.example.trendlog.repository.post;

import com.example.trendlog.domain.post.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserIdAndPostId(UUID userId, Long postId);
    void deleteByUserIdAndPostId(UUID userId, Long postId);
}
