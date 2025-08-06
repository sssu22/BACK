package com.example.trendlog.service;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.dto.response.trend.TrendRecommendScoreDto;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.MailErrorCode;
import com.example.trendlog.global.exception.code.PythonErrorCode;
import com.example.trendlog.global.exception.code.TrendErrorCode;
import com.example.trendlog.repository.post.PostRepository;
import com.example.trendlog.repository.trend.TrendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            throw new AppException(PythonErrorCode.PYTHON_EXEC_FAIL);
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
                trend.updateScore((int) trendScore);
            } catch (Exception e) {
                throw new AppException(TREND_SCORE_UPDATE_FAIL);
            }
        }
        log.info("모든 트렌드의 총 점수 업데이트 완료");
    }

}
