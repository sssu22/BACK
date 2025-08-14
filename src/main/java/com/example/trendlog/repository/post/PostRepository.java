package com.example.trendlog.repository.post;

import com.example.trendlog.domain.user.User;
import com.example.trendlog.domain.post.Emotion;
import com.example.trendlog.domain.post.Post;
import com.example.trendlog.domain.trend.Trend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    // 삭제되지 않은 게시글을 최신순으로 조회
    Page<Post> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    // 삭제가 되지 않은 게시글을 게시글 ID로 조회
    Optional<Post> findByIdAndDeletedFalse(Long postId);

    // 삭제가 되지 않은 게시글을 감정 타입으로 조회
    Page<Post> findAllByDeletedFalseAndEmotion(Emotion emotion, Pageable pageable);

    Page<Post> findAllByDeletedFalse(Pageable pageable);


    @Query("SELECT p.district, COUNT(p) " +
            "FROM Post p " +
            "WHERE p.user.id = :userId AND p.district IS NOT NULL AND p.deleted = false " +
            "GROUP BY p.district")
    List<Object[]> countPostsByDistrictForUser(@Param("userId") UUID userId);

    Page<Post> findAllByDeletedFalseAndUserId(UUID userId, Pageable pageable);

    Page<Post> findAllByDeletedFalseAndUserIdAndDistrict(UUID userId, String district, Pageable pageable);

    int countByTrendAndDeletedFalse(Trend trend);

    @Query("SELECT COALESCE(SUM(p.likeCount), 0) FROM Post p WHERE p.trend = :trend AND p.deleted = false")
    int sumLikesByTrend(@Param("trend") Trend trend);

    int countByUserAndDeletedFalse(User user);

    @Query("SELECT AVG(t.score) FROM Post p JOIN p.trend t WHERE p.user = :user AND p.deleted = false")
    Integer findAverageTrendScoreByUser(@Param("user") User user);

    @Query("SELECT COUNT(DISTINCT p.district) FROM Post p WHERE p.user=:user AND p.deleted=false")
    int countDistinctDistrictsByUser(@Param("user") User user);

    List<Post> findTop3ByUserAndDeletedFalseOrderByCreatedAtDesc(User user);

    //일단 기간은 고려 안함
    @Query("SELECT p.user.id, p.trend.id, COUNT(p) * 6 FROM Post p WHERE p.deleted = false GROUP BY p.user.id, p.trend.id")
    List<Object[]> fetchPostWriteScores();

    int countByTrendAndCreatedAtBetweenAndDeletedFalse(Trend trend, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.trend.id = :trendId")
    int countByTrendId(@Param("trendId") Long trendId);

}
