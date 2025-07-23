package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.RecentTrend;
import com.example.trendlog.domain.trend.Trend;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RecentTrendResponse {
    private final Long trendId;
    private final String title;
    private final String tag;
    private final Integer score;
    private final Integer increaseScore;

    @Builder
    public RecentTrendResponse(Long trendId, String title, String tag, Integer score, Integer increaseScore) {
        this.trendId = trendId;
        this.title = title;
        this.tag = tag;
        this.score = score;
        this.increaseScore = increaseScore;
    }

    public static RecentTrendResponse from(RecentTrend recentTrend) {
        Trend trend= recentTrend.getTrend();
        String firstTag=(trend.getTags() != null && !trend.getTags().isEmpty())
                ? trend.getTags().get(0)
                : null;
        return RecentTrendResponse.builder()
                .trendId(trend.getId())
                .title(trend.getTitle())
                .tag(firstTag)
                .score(recentTrend.getTrendScore())
                .increaseScore(recentTrend.getIncreaseScore())
                .build();
    }

}
