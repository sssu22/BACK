package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendScrap;
import com.example.trendlog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrendScrapRepository extends JpaRepository<TrendScrap, Long> {
    boolean existsByUserAndTrend(User user, Trend trend);
    Optional<TrendScrap> findByUserAndTrend(User user, Trend trend);
}
