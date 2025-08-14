package com.example.trendlog.service.trend;

import com.example.trendlog.domain.trend.TrendPrediction;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.repository.trend.TrendPredictionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static com.example.trendlog.global.exception.code.TrendErrorCode.CSV_READ_FAIL;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrendForecastJob {
    private final WebClient fastApiWebClient;
    private final TrendScoreCsvExporter trendScoreCsvExporter;   // 기존 컴포넌트
    private final TrendPredictionRepository trendPredictionRepository; // 기존 리포지토리

    private static final Path SHARED = Path.of("/shared");
    private static final Path SCORE_CSV = SHARED.resolve("trend_scores.csv");
    private static final Path PREDICT_CSV = SHARED.resolve("predicted_top3.csv");

    // 월요일 02:30
    @Scheduled(cron = "0 30 2 * * MON", zone = "Asia/Seoul")
    public void runWeeklyForecast() {
        try {
            // 1) 모든 점수 CSV로 export (반드시 /shared 경로로 쓰게 수정)
            trendScoreCsvExporter.exportAllTrendScoresToCsv();

            runProphetScript();
        } catch (Exception e) {
            log.error("주간 예측 파이프라인 실패", e);
        }
    }

    @Scheduled(cron = "0 40 2 * * MON", zone = "Asia/Seoul")
    public void storeWeeklyForecast() {
        try{
            // 3) 예측 결과 CSV import (/shared/predicted_top3.csv)
            importPredictionCsv();
        }catch (Exception e){
            log.error("예측 결과 저장 실패", e);
        }

    }

    public void runProphetScript(){
        // 2) FastAPI에 예측 실행 요청
        fastApiWebClient.post()
                .uri("/jobs/trends/forecast/weekly")
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> log.info("FastAPI weekly forecast queued: {}", body))
                .block();
    }

    public void importPredictionCsv() {
        try {
            trendPredictionRepository.deleteAll();

            if (!Files.exists(PREDICT_CSV)) {
                log.error("{} 파일이 없습니다.", PREDICT_CSV);
                return;
            }

            try (BufferedReader reader = Files.newBufferedReader(PREDICT_CSV)) {
                String line;
                boolean isFirst = true;
                while ((line = reader.readLine()) != null) {
                    if (isFirst) { isFirst = false; continue; }
                    String[] tokens = line.split(",");
                    if (tokens.length < 3) continue;

                    Long trendId = Long.parseLong(tokens[0]);
                    Double increaseRate = Double.parseDouble(tokens[1]);
                    Double confidence = Double.parseDouble(tokens[2]);

                    TrendPrediction prediction = TrendPrediction.builder()
                            .trendId(trendId)
                            .increaseRate(increaseRate)
                            .confidence(confidence)
                            .predictionDate(LocalDate.now())
                            .build();
                    trendPredictionRepository.save(prediction);
                }
            }
            log.info("{} → 예측 결과 저장 완료", PREDICT_CSV);

        } catch (IOException e) {
            log.error("예측 CSV 읽기 실패", e);
            throw new AppException(CSV_READ_FAIL);
        }
    }
}
