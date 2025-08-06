package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.TrendScore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TrendScoreRepository extends JpaRepository<TrendScore, Long> {
    boolean existsByTrendIdAndDate(Long trendId, LocalDate date);
}