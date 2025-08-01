package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.User;
import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendViewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrendViewLogRepository extends JpaRepository<TrendViewLog,Long> {
    boolean existsByUserAndTrend(User user, Trend trend);
}
