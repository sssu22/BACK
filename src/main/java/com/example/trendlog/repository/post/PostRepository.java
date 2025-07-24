package com.example.trendlog.repository.post;

import com.example.trendlog.domain.post.Emotion;
import com.example.trendlog.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 삭제되지 않은 게시글을 최신순으로 조회
    Page<Post> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    // 삭제가 되지 않은 게시글을 게시글 ID로 조회
    Optional<Post> findByIdAndDeletedFalse(Long postId);

    // 삭제가 되지 않은 게시글을 감정 타입으로 조회
    Page<Post> findAllByDeletedFalseAndEmotion(Emotion emotion, Pageable pageable);

    Page<Post> findAllByDeletedFalse(Pageable pageable);


    @Query("SELECT p.district, COUNT(p) " +
            "FROM Post p " +
            "WHERE p.user.id = :userId AND p.district IS NOT NULL " +
            "GROUP BY p.district")
    List<Object[]> countPostsByDistrictForUser(@Param("userId") UUID userId);

    Page<Post> findAllByDeletedFalseAndUserId(UUID userId, Pageable pageable);

    Page<Post> findAllByDeletedFalseAndUserIdAndDistrict(UUID userId, String district, Pageable pageable);
}
