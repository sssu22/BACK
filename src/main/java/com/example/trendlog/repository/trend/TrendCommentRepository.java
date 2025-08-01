package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrendCommentRepository extends JpaRepository<TrendComment, Long> {
    List<TrendComment> findByTrendOrderByCreatedAtDesc(Trend trend);

    @Query("SELECT c.user.id, c.trend.id, COUNT(c) * 5 FROM TrendComment c GROUP BY c.user.id, c.trend.id")
    List<Object[]> fetchTrendCommentScores();

}
