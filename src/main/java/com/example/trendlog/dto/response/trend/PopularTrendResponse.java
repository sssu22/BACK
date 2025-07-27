package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.PopularTrend;
import com.example.trendlog.domain.trend.Trend;
import lombok.Builder;
import lombok.Getter;
@Builder
@Getter
public class PopularTrendResponse {
    private final Long trendId;
    private final String title;
    private final String tag;
    private final Integer score;

    public static PopularTrendResponse from(PopularTrend popularTrend) {
        String firstTag = (popularTrend.getTrend().getTags() != null && !popularTrend.getTrend().getTags().isEmpty())
                ? popularTrend.getTrend().getTags().get(0)
                : null;

        return PopularTrendResponse.builder()
                .trendId(popularTrend.getTrend().getId())
                .title(popularTrend.getName()) // 저장 당시 이름
                .tag(firstTag)
                .score(popularTrend.getTrendScore())
                .build();
    }
}
