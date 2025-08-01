package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.User;
import com.example.trendlog.domain.trend.UserTrendRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserTrendRecommendationRepository extends JpaRepository<UserTrendRecommendation, Long> {
    List<UserTrendRecommendation> findTop3ByUserOrderByRankAsc(User user);
    void deleteByUser(User user);
}
