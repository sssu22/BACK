package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;
@Getter
@Builder
public class SimilarTrendDto {
    private final Long trendId;
    private final String title;
    private final List<String> tags;
    private final Integer score;

    public static SimilarTrendDto from(Trend trend) {
        return new SimilarTrendDto(
                trend.getId(),
                trend.getTitle(),
                trend.getTags() != null ? trend.getTags() : List.of(),
                trend.getScore()
        );
    }
}
