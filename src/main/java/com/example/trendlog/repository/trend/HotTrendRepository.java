package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.HotTrend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface HotTrendRepository extends JpaRepository<HotTrend, Long> {
    @Query("SELECT MAX(r.period) FROM HotTrend r")
    LocalDateTime findLatestPeriod();

    List<HotTrend> findTop3ByPeriodOrderByIncreaseScoreDesc(LocalDateTime period);
}
