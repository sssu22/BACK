package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecommendedTrendResponse {
    private final Long trendId;
    private final String title;
    private final String description;
    private final String category;
    private final Integer score;
    private final Boolean isScrapped;

    public static RecommendedTrendResponse from(Trend trend, boolean isScrapped) {
        return RecommendedTrendResponse.builder()
                .trendId(trend.getId())
                .title(trend.getTitle())
                .description(trend.getDescription())
                .category(trend.getDescription())
                .score(trend.getScore())
                .isScrapped(isScrapped)
                .build();
    }
}
