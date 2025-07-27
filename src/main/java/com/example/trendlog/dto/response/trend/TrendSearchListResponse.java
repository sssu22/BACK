package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;

public record TrendSearchListResponse(
        Long id,
        String title,
        String category
) {
    public static TrendSearchListResponse from(Trend trend) {
        return new TrendSearchListResponse(
                trend.getId(),
                trend.getTitle(),
                trend.getCategory().name()
        );
    }
}
