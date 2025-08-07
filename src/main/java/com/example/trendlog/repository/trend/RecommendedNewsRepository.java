package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.RecommendedNews;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendedNewsRepository extends JpaRepository<RecommendedNews, Long> {
}