package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrendCommentRepository extends JpaRepository<TrendComment, Long> {
    List<TrendComment> findByTrendOrderByCreatedAtDesc(Trend trend);
}
