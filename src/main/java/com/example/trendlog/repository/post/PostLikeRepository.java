package com.example.trendlog.repository.post;

import com.example.trendlog.domain.post.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByUserIdAndPostId(UUID userId, Long postId);
    void deleteByUserIdAndPostId(UUID userId, Long postId);

    @Query("SELECT pl.user.id, pl.post.trend.id, COUNT(pl) * 2 FROM PostLike pl WHERE pl.post.trend IS NOT NULL GROUP BY pl.user.id, pl.post.trend.id")
    List<Object[]> fetchPostLikeScores();
}
