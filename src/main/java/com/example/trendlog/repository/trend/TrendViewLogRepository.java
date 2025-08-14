package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.TrendViewLog;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface TrendViewLogRepository extends JpaRepository<TrendViewLog,Long> {
    @Query(value = """
    SELECT l.user_id, l.trend_id, COUNT(DISTINCT DATE(l.viewed_at)) * 1
    FROM trend_view_log l
    WHERE l.viewed_at >= :start
    GROUP BY l.user_id, l.trend_id
    """, nativeQuery = true)
    List<Object[]> fetchTrendViewScores(@Param("start") LocalDateTime start);

}
