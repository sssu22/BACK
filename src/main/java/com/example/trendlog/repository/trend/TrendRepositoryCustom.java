package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TrendRepositoryCustom {
    Page<Trend> searchAll(TrendSearchCondition condition, Pageable pageable);
    Page<Trend> searchScrapped(TrendSearchCondition condition, Pageable pageable);

}
