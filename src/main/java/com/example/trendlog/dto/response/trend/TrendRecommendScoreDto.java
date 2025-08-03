package com.example.trendlog.dto.response.trend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
public class TrendRecommendScoreDto {
    @Schema(description = "사용자 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private final UUID userId;

    @Schema(description = "트렌드 ID", example = "42")
    private final Long trendId;

    @Schema(description = "추천 점수", example = "15")
    private final Integer score;
    public TrendRecommendScoreDto(UUID userId, Long trendId, Integer score) {
        this.userId = userId;
        this.trendId = trendId;
        this.score = score;
    }
    public static TrendRecommendScoreDto from(UUID userId, Long trendId, int score) {
        return new TrendRecommendScoreDto(userId, trendId, score);
    }
}
