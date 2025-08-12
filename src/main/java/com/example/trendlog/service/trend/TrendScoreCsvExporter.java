package com.example.trendlog.service.trend;

import com.example.trendlog.domain.trend.TrendScore;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.repository.trend.TrendScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static com.example.trendlog.global.exception.code.TrendErrorCode.TREND_SCORE_EXPORT_FAIL;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrendScoreCsvExporter {

    private final TrendScoreRepository trendScoreRepository;

    public void exportAllTrendScoresToCsv() {
        List<TrendScore> scores = trendScoreRepository.findAll();

        String filePath = System.getProperty("user.dir") + "/ai-recommendation/trend_score.csv";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // CSV 헤더
            writer.write("trend_id,date,score");
            writer.newLine();

            for (TrendScore score : scores) {
                writer.write(String.format("%d,%s,%d",
                        score.getTrend().getId(),
                        score.getDate(),
                        score.getScore()
                ));
                writer.newLine();
            }

            log.info("trend_score.csv 파일 생성 완료: {}", filePath);

        } catch (IOException e) {
            log.error("trend_score.csv 파일 생성 실패", e);
            throw new AppException(TREND_SCORE_EXPORT_FAIL);
        }
    }
}

