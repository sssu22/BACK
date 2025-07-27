package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.RecentTrend;
import com.example.trendlog.domain.trend.Trend;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RecentTrendResponse {
    private final Long trendId;
    private final String title;
    private final String tag;
    private final Integer score;
    private final Integer increaseScore;

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
