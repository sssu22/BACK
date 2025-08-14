package com.example.trendlog.controller;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.dto.response.trend.TrendRecommendScoreDto;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.PythonErrorCode;
import com.example.trendlog.service.external.YoutubeApiService;
import com.example.trendlog.service.trend.*;
import com.example.trendlog.repository.trend.TrendRepository;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 스케줄러 강제 실행 코드
 */
@Tag(name = "Scheduler", description = "스케쥴러를 강제 실행하기 위한 API(테스트용)")
@RestController
@RequestMapping("/test/scheduler")
@RequiredArgsConstructor
public class SchedulerTestController {
    private final TrendService trendService;
    private final TrendRecommendScoreExportService trendRecommendScoreExportService;
    private final TrendExportService trendExportService;
    private final TrendRecommendCsvImportService trendRecommendCsvImportService;
    private final TrendRepository trendRepository;
    private final YoutubeApiService youtubeApiService;
    private final TrendScoreCsvExporter trendScoreCsvExporter;
    private final TrendStatisticsScheduler trendStatisticsScheduler;
    private final TrendStatisticsScheduler trendScheduler; // triggerDailyRecommend가 있는 클래스
    private final TrendForecastJob trendForecastJob;

    @PostMapping("/popular")
    public ResponseEntity<Void> runPopularTrendJob() {
        trendService.updatePopularTrends();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/recent-trends")
    public ResponseEntity<String> runRecentTrendScheduler() {
        trendService.updateRecentTrends();
        return ResponseEntity.ok("최근 트렌드 스케줄러 실행 완료");
    }

    @GetMapping("/export-score-csv")
    public ResponseEntity<String> exportCsv() {
        List<TrendRecommendScoreDto> scores = trendRecommendScoreExportService.getAllTrendScores();
        trendRecommendScoreExportService.exportScoresToCsv(scores);
        return ResponseEntity.ok("score CSV export 완료");
    }
    @GetMapping("/export-trend-csv")
    public ResponseEntity<String> exportTrendCsv() {
        trendExportService.exportAllTrendsToCsv();
        return ResponseEntity.ok("trend CSV export 완료");
    }

    @GetMapping("/peak")
    public ResponseEntity<String> peakTrend() {
        trendService.updatePeakPeriods();
        return ResponseEntity.ok("피크타임 갱신 완료");
    }

    @GetMapping("/save-recommend")
    public ResponseEntity<String> importDailyRecommendation() {
        String path = "/shared/recommended_trends.csv";
        trendRecommendCsvImportService.importFromCsv(path);
        return ResponseEntity.ok("추천 결과 CSV → DB 저장 완료");

    }

    @GetMapping("/run-python")
    public ResponseEntity<String> runPython() {
//        try {
//            ProcessBuilder pb = new ProcessBuilder("python3", "ai-recommendation/recommend.py");
//            pb.inheritIO(); // 로그 출력 확인용
//            Process process = pb.start();
//            return ResponseEntity.ok("파이썬 실행 완료->추천 CSV 생성");
//        } catch (Exception e) {
//            throw new AppException(PythonErrorCode.PYTHON_EXEC_FAIL);
//        }
        try {
            trendScheduler.triggerDailyRecommend(); // 스케줄러 메소드 직접 호출
            return ResponseEntity.ok("FastAPI daily recommend 호출 완료");
        } catch (Exception e) {
            throw new AppException(PythonErrorCode.PYTHON_EXEC_FAIL);
        }

    }
    @GetMapping("youtube")
    @Transactional
    public ResponseEntity<String> youtubeTrend() {
        String[] range= YoutubeApiService.getLastMonthRange();
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
        return ResponseEntity.ok("유튜브 언급량 성공");

    }

    // 시계열 예측
    @PostMapping("/predict")
    public ResponseEntity<String> runTrendPredictionManually() {
//        trendScoreCsvExporter.exportAllTrendScoresToCsv(); // CSV 생성
//        trendStatisticsScheduler.runProphetScript();         // Python 실행
        trendForecastJob.runProphetScript();
        trendStatisticsScheduler.importPredictionCsv();      // 결과 저장
        return ResponseEntity.ok("트렌드 예측 수동 실행 완료");
    }
}
