package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.HotTrend;
import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendCategory;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TrendRepository extends JpaRepository<Trend, Long> {
    // 최근 일주일 내에 생성된 트렌드 중 상위 점수순 정렬 (popular)
    List<Trend> findTop5ByCreatedAtAfterOrderByScoreDesc(java.time.LocalDateTime after);

    @Query("SELECT t FROM Trend t ORDER BY (t.score - t.previousScore) DESC")
    List<Trend> findTop3ByScoreIncrease(Pageable pageable);

    boolean existsByTitle(String title);

    List<Trend> findByCategory(TrendCategory category);

    List<Trend> findByTitleContaining(String title);

}
