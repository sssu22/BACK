package com.example.trendlog.repository.post;

import com.example.trendlog.domain.User;
import com.example.trendlog.domain.post.PostScrap;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {
    boolean existsByUserIdAndPostId(UUID user_id, Long postId);
    void deleteByUserIdAndPostId(UUID user_id, Long post_id);

    @Query("SELECT COUNT(ps) FROM PostScrap ps WHERE ps.user = :user AND ps.post.deleted = false")
    int countValidScrapByUser(@Param("user")User user);

    List<PostScrap> findByUserAndPostDeletedFalseOrderByCreatedAtDesc(User user);


}
