package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendLike;
import com.example.trendlog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TrendLikeRepository extends JpaRepository<TrendLike, Long> {
    Optional<TrendLike> findByUserAndTrend(User user, Trend trend);
    boolean existsByTrendAndUser(Trend trend, User user);


}
