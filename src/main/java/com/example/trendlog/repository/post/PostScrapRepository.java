package com.example.trendlog.repository.post;

import com.example.trendlog.domain.post.PostScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {
    boolean existsByUserIdAndPostId(UUID user_id, Long postId);
    void deleteByUserIdAndPostId(UUID user_id, Long post_id);
}
