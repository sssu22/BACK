package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TrendListResponse {
    private final Long trendId;
    private final String title;
    private final String tag;
    private final Integer score;

    @Builder
    public TrendListResponse(Long trendId, String title, String tag, Integer score) {
        this.trendId = trendId;
        this.title = title;
        this.tag = tag;
        this.score = score;
    }

    public static TrendListResponse from(Trend trend) {
        String firstTag = (trend.getTags() != null && !trend.getTags().isEmpty())
                ? trend.getTags().get(0)
                : null;
        return TrendListResponse.builder()
                .trendId(trend.getId())
                .title(trend.getTitle())
                .tag(firstTag)
                .score(trend.getScore())
                .build();
    }
}
