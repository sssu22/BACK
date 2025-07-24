package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;

import java.util.List;
import java.util.UUID;

public class SimilarTrendDto {
    private final Long trendId;
    private final String title;
    private final List<String> tags;
    private final Integer score;

    public SimilarTrendDto(Long trendId, String title, List<String> tags, Integer score) {
        this.trendId = trendId;
        this.title = title;
        this.tags = tags;
        this.score = score;
    }

    public static SimilarTrendDto from(Trend trend) {
        return new SimilarTrendDto(
                trend.getId(),
                trend.getTitle(),
                trend.getTags() != null ? trend.getTags() : List.of(),
                trend.getScore()
        );
    }
}
