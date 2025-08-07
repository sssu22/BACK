package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.TrendPrediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrendPredictionRepository extends JpaRepository<TrendPrediction, Long> {
    List<TrendPrediction> findTop3ByOrderByIncreaseRateDesc();
}
