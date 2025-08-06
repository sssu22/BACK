package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendPrediction;

public record TrendPredictionResponse(
        Long trendId,
        String title,
        String description,
        Integer increaseRate,
        Integer confidence
) {
    public static TrendPredictionResponse from(TrendPrediction prediction, Trend trend) {
        return new TrendPredictionResponse(
                prediction.getTrendId(),
                trend.getTitle(),
                trend.getDescription(),
                (int) Math.round(prediction.getIncreaseRate()),
                (int) Math.round(prediction.getConfidence())
        );
    }
}
