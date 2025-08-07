package com.example.trendlog.service;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendPrediction;
import com.example.trendlog.dto.response.trend.TrendRecommendScoreDto;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.repository.post.PostRepository;
import com.example.trendlog.repository.trend.TrendPredictionRepository;
import com.example.trendlog.repository.trend.TrendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.trendlog.global.exception.code.PythonErrorCode.PYTHON_EXEC_FAIL;
import static com.example.trendlog.global.exception.code.TrendErrorCode.CSV_READ_FAIL;
import static com.example.trendlog.global.exception.code.TrendErrorCode.TREND_SCORE_UPDATE_FAIL;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrendStatisticsScheduler {
    private final TrendRecommendScoreExportService trendRecommendScoreExportService;
    private final TrendExportService trendExportService;
    private final TrendService trendService;
    private final TrendRecommendCsvImportService trendRecommendCsvImportService;
    private final TrendRepository trendRepository;
    private final YoutubeApiService youtubeApiService;
    private final PostRepository postRepository;
    private final TrendScoreCsvExporter trendScoreCsvExporter;
    private final TrendPredictionRepository trendPredictionRepository;

    @Scheduled(cron = "0 50 2 * * *")
    public void updateRecommendationScores() {
        List<TrendRecommendScoreDto> scores = trendRecommendScoreExportService.getAllTrendScores();
        trendRecommendScoreExportService.exportScoresToCsv(scores);
        log.info("추천 점수 계산 완료. 총 건수: {} " , scores.size());
    }

    @Scheduled(cron = "0 51 2 * * *")
    public void updateTrendCsvFile() {
        trendExportService.exportAllTrendsToCsv();
        log.info("전체 트렌드 csv 파일 생성 완료.");
    }

    @Scheduled(cron = "0 57 2 * * *")
    public void runPythonScript() {
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "ai-recommendation/recommend.py");
            pb.inheritIO(); // 로그 출력 확인용
            Process process = pb.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("Python 추천 스크립트 실행 완료");
            } else {
                log.error("Python 추천 스크립트 실패. 종료코드: {}", exitCode);
            }
        } catch (Exception e) {
            throw new AppException(PYTHON_EXEC_FAIL);
        }
    }

    @Scheduled(cron = "0 0 0 1 * *") // 매월 1일 00:00에 실행
    public void updatePeakPeriods() {
        trendService.updatePeakPeriods();
        log.info("피크시기 갱신 완료");
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void importDailyRecommendation() {
        String path = "ai-recommendation/recommended_trends.csv";
        trendRecommendCsvImportService.importFromCsv(path);
        log.info("추천 결과 CSV → DB 저장 완료");
    }

    @Scheduled(cron = "0 0 0 1 * *") // 매월 1일 00:00
    @Transactional
    public void updateTrendsYoutubeMentions(){
        String[] range=YoutubeApiService.getLastMonthRange();
        String publishedAfter=range[0];
        String publishedBefore=range[1];

        List<Trend>trends=trendRepository.findAll();
        for(Trend trend:trends){
            List<String> videosIds=youtubeApiService.getYoutubeVideoIds(trend.getTitle(), publishedAfter, publishedBefore);
            int mentionCount=videosIds.size();
            Long topViews= youtubeApiService.getTotalViewsOfTopNVideos(videosIds,10);

            trend.setSnsMentions(mentionCount); // 영상 개수
            trend.setYoutubeTopView(topViews); // 누적 조회수
        }
        trendRepository.saveAll(trends);
    }

    @Scheduled(cron = "0 0 2 * * MON") // 매주 월요일 새벽 2시
    @Transactional
    public void updateTrendTotalScores() {
        int maxSearchVolume = trendRepository.findMaxSearchVolume();
        List<Trend> trends = trendRepository.findAll();

        int maxActivityScore = 0;
        Map<Long, Integer> postCountMap = new HashMap<>();

        // 먼저 모든 trend의 게시글 수를 미리 가져오기
        for (Trend trend : trends) {
            int postCount = postRepository.countByTrendId(trend.getId());
            postCountMap.put(trend.getId(), postCount);

            int activityScore = trend.getLikeCount() + trend.getScrapCount() + postCount;
            maxActivityScore = Math.max(maxActivityScore, activityScore);
        }

        for (Trend trend : trends) {
            try {
                int postCount = postCountMap.getOrDefault(trend.getId(), 0);
                int activity = trend.getLikeCount() + trend.getScrapCount() + postCount;

                double normalizedActivity = (maxActivityScore == 0) ? 0 : (double) activity / maxActivityScore * 100;
                double normalizedSearch = (maxSearchVolume == 0) ? 0 : (double) trend.getSearchVolume() / maxSearchVolume * 100;

                double trendScore = (trend.getNewsScore() + normalizedActivity + normalizedSearch) / 3.0;
                trendService.updateTrendScore(trend, (int) trendScore);
            } catch (Exception e) {
                throw new AppException(TREND_SCORE_UPDATE_FAIL);
            }
        }
        log.info("모든 트렌드의 총 점수 업데이트 완료");
    }

    // 트렌드 시계열 분석
    @Scheduled(cron = "0 30 2 * * MON")
    public void runTrendPredictionPipeline() {
        trendScoreCsvExporter.exportAllTrendScoresToCsv(); // CSV 생성
        runProphetScript(); // ProcessBuilder로 recommend_trends_time_series.py 실행
        importPredictionCsv(); // 예측 결과 DB 저장
    }


    public void runProphetScript() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "python3",
                    "./ai-recommendation/recommend_trends_time_series.py"
            );
            pb.directory(new File(System.getProperty("user.dir")));
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[Prophet] " + line);
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.info("Prophet 예측 스크립트 실행 성공");
            } else {
                log.error("Prophet 스크립트 실행 실패. 종료 코드: " + exitCode);
            }

        } catch (Exception e) {
            throw new AppException(PYTHON_EXEC_FAIL);
        }
    }

    public void importPredictionCsv() {
        // 중복 저장하지 않기 위해 저장 전 데이터 모두 삭제
        trendPredictionRepository.deleteAll();

        String filePath = System.getProperty("user.dir") + "/ai-recommendation/predicted_top3.csv";
        File file = new File(filePath);

        if (!file.exists()) {
            log.error("predicted_top3.csv 파일이 존재하지 않습니다.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;

            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // skip header
                }

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

            log.info("predicted_top3.csv 파일을 DB에 저장 완료");

        } catch (IOException e) {
            log.error("predicted_top3.csv 읽기 실패", e);
            throw new AppException(CSV_READ_FAIL);
        }
    }
}
