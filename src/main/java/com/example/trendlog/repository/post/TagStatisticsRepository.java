package com.example.trendlog.repository.post;

import com.example.trendlog.domain.post.PostStatistics;
import com.example.trendlog.domain.post.TagStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TagStatisticsRepository extends JpaRepository<TagStatistics, Long> {

    @Modifying
    @Query(value = """
    INSERT INTO tag_statistics (tag_id, total_count)
    SELECT tag_id, COUNT(*) AS total_count
    FROM post_tag
    GROUP BY tag_id
    ON CONFLICT (tag_id)
    DO UPDATE SET total_count = EXCLUDED.total_count
    """, nativeQuery = true)
    void upsertAllStatistics();


    @Query("SELECT ts FROM TagStatistics ts JOIN FETCH ts.tag t ORDER BY ts.totalCount DESC, t.createdAt DESC")
    Page<TagStatistics> findAllOrderByTotalCountDescTagCreatedAtDesc(Pageable pageable);


}
