package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TrendStatisticsResponse {
    private final Long trendId;
    private final String trendName;
    private final Integer score;
    private final Integer postCount;
    private final Integer trendLikeCount;
    private final Integer relatedPostLikeCount;
    private final Integer scrapCount;
    private final Integer snsMentionCount; //일단 임의값
    private final Integer searchCount; //일단 임의값

    public static TrendStatisticsResponse from(
            Trend trend,
            int postCount,
            int relatedPostLikeCount,
            int snsMentionCount,
            int searchCount
    ) {
        return TrendStatisticsResponse.builder()
                .trendId(trend.getId())
                .trendName(trend.getTitle())
                .score(trend.getScore())
                .postCount(postCount)
                .trendLikeCount(trend.getLikeCount())
                .relatedPostLikeCount(relatedPostLikeCount)
                .scrapCount(trend.getScrapCount())
                .snsMentionCount(snsMentionCount)
                .searchCount(searchCount)
                .build();
    }
}
