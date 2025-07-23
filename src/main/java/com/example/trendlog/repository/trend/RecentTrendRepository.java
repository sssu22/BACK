package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.RecentTrend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface RecentTrendRepository extends JpaRepository<RecentTrend, Long> {
    @Query("SELECT MAX(r.period) FROM RecentTrend r")
    LocalDateTime findLatestPeriod();

    List<RecentTrend> findTop3ByPeriodOrderByIncreaseScoreDesc(LocalDateTime period);
}
