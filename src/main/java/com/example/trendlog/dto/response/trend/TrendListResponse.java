package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;
import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class TrendListResponse {
    private final Long trendId;
    private final String title;
    private final String tag;
    private final String description;
    private final Integer score;
    private final String categoryName;

    public static TrendListResponse from(Trend trend) {
        String firstTag = (trend.getTags() != null && !trend.getTags().isEmpty())
                ? trend.getTags().get(0)
                : null;
        return TrendListResponse.builder()
                .trendId(trend.getId())
                .title(trend.getTitle())
                .description(trend.getDescription())
                .score(trend.getScore())
                .tag(firstTag)
                .categoryName(trend.getCategory().getDescription())
                .build();
    }
}
