package com.example.trendlog.service;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendPrediction;
import com.example.trendlog.dto.response.trend.TrendPredictionResponse;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.repository.trend.TrendPredictionRepository;
import com.example.trendlog.repository.trend.TrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.trendlog.global.exception.code.TrendErrorCode.TREND_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TrendPredictionService {

    private final TrendPredictionRepository trendPredictionRepository;
    private final TrendRepository trendRepository;

    public List<TrendPredictionResponse> getTop3LatestPredictions() {
        List<TrendPrediction> predictions = trendPredictionRepository.findTop3ByOrderByIncreaseRateDesc();

        return predictions.stream()
                .map(prediction -> {
                    Trend trend = trendRepository.findById(prediction.getTrendId())
                            .orElseThrow(() -> new AppException(TREND_NOT_FOUND));
                    return TrendPredictionResponse.from(prediction, trend);
                })
                .toList();
    }
}
