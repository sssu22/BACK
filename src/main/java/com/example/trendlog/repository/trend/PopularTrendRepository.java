package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.PopularTrend;
import com.example.trendlog.domain.trend.TrendLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PopularTrendRepository extends JpaRepository<PopularTrend, Long> {
    List<PopularTrend> findByPeriodBetween(LocalDateTime start, LocalDateTime end);
    List<PopularTrend> findTop5ByPeriodOrderByTrendScoreDesc(LocalDateTime period);
    @Query("SELECT MAX(p.period) FROM PopularTrend p")
    LocalDateTime findLatestPeriod();

}
