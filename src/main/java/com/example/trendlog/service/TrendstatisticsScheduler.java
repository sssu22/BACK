package com.example.trendlog.service;

import com.example.trendlog.dto.response.trend.TrendRecommendScoreDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrendstatisticsScheduler {
    private final TrendRecommendScoreExportService trendRecommendScoreExportService;

    @Scheduled(cron = "0 0 3 * * *") // 매일 3시
    public void updateRecommendationScores() {
        List<TrendRecommendScoreDto> scores = trendRecommendScoreExportService.getAllTrendScores();
        trendRecommendScoreExportService.exportScoresToCsv(scores);
        log.info("추천 점수 계산 완료. 총 건수: {} " , scores.size());
    }
}
