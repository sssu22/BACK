package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.post.PostScrap;
import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendScrap;
import com.example.trendlog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrendScrapRepository extends JpaRepository<TrendScrap, Long> {
    boolean existsByTrendAndUser(Trend trend, User user);
    Optional<TrendScrap> findByUserAndTrend(User user, Trend trend);
    int countByUser(User user);
    List<TrendScrap> findByUserOrderByCreatedAtDesc(User user);

}
