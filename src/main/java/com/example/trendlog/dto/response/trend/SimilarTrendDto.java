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
    private final String tag;
    private final Integer score;

    public static SimilarTrendDto from(Trend trend) {
        // tags에서 첫 번째 태그만 반환
        String tag = (trend.getTags() != null && !trend.getTags().isEmpty())
                ? trend.getTags().get(0)
                : "";
        return new SimilarTrendDto(
                trend.getId(),
                trend.getTitle(),
                tag,
                trend.getScore()
        );
    }
}
