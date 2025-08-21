package com.example.trendlog.repository.post;

import com.example.trendlog.domain.post.PostStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostStatisticsRepository extends JpaRepository<PostStatistics, Long> {
    // 24시간 내 작성된 커뮤니티 게시글에 대한 좋아요와 댓글 수를 합산하여 통계 정보를 업데이트
    @Modifying
    @Query(value = """
    INSERT INTO post_statistics (post_id, total_count)
    SELECT post_id,
           like_count + comment_count AS total_count
    FROM posts
    WHERE created_at >= CURRENT_TIMESTAMP - INTERVAL '7 day' AND deleted = false
    """, nativeQuery = true)
    void updateCommunityStatistics();

//    @EntityGraph(attributePaths = {
//            "post"
//    })
//    Page<PostStatistics> findAllByOrderByTotalCountDesc(Pageable pageable);
    @Query("""
    SELECT ps FROM PostStatistics ps
    JOIN FETCH ps.post p
    WHERE p.deleted = false
    ORDER BY ps.totalCount DESC, p.createdAt DESC
    """)
    Page<PostStatistics> findAllOrderByTotalCountDescPostCreatedAtDesc(Pageable pageable);

    @Modifying
    @Query("DELETE FROM PostStatistics")
    void deleteAllStatistics();

}
