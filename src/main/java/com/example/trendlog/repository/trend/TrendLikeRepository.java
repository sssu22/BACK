package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendLike;
import com.example.trendlog.domain.User;
import com.example.trendlog.dto.response.trend.TrendRecommendScoreDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TrendLikeRepository extends JpaRepository<TrendLike, Long> {
    Optional<TrendLike> findByUserAndTrend(User user, Trend trend);

    boolean existsByTrendAndUser(Trend trend, User user);

    @Query("SELECT l.user.id, l.trend.id,  3 from TrendLike l GROUP BY l.user.id, l.trend.id")
    List<Object[]> fetchTrendLikeScores();

}
