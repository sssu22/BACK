package com.example.trendlog.service;

import com.example.trendlog.domain.trend.RecommendedNews;
import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.repository.trend.TrendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static com.example.trendlog.global.exception.code.TrendNewsErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsRecommendationService {

    private final TrendRepository trendRepository;

    public List<RecommendedNews> generateNewsForKeyword(String keyword) {
        String safeKeyword = keyword.replaceAll("\\s+", "_");
        String filename = "recommended_news_" + safeKeyword + "_temp.csv";
        Path csvPath = Paths.get("./output/" + filename);

        // 1. Python 스크립트 실행
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", "./ai-recommendation/recommend_news.py", keyword);
            pb.directory(new File(System.getProperty("user.dir")));
            pb.redirectErrorStream(true);

            Process process = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[PYTHON] " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new AppException(PYTHON_EXEC_FAIL);
            }

        } catch (IOException | InterruptedException e) {
            throw new AppException(PYTHON_EXEC_FAIL);
        }

        // 2. CSV 읽기
        List<RecommendedNews> result = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                String[] tokens = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (tokens.length == 4) {
                    RecommendedNews news = RecommendedNews.builder()
                            .keyword(tokens[0])
                            .title(tokens[1])
                            .link(tokens[2])
                            .score(Integer.parseInt(tokens[3]))
                            .build();

                    result.add(news);
                }
            }
        } catch (IOException e) {
            throw new AppException(CSV_READ_FAIL);
        } finally {
            // csv 파일 만든 후 삭제
            try {
                Files.deleteIfExists(csvPath);
                System.out.println("임시 파일 삭제 완료: " + csvPath);
            } catch (IOException e) {
                System.err.println("임시 파일 삭제 실패: " + e.getMessage());
            }
        }

        return result;
    }

    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 00시
    @Transactional
    public void refreshNewsAndScore() {
        List<Trend> trends = trendRepository.findAll();

        for (Trend trend : trends) {
            try {
                // 기존 뉴스 삭제
                trend.clearRecommendedNews(); // 연관관계 제거

                // 새로운 뉴스 + 점수 가져오기
                List<RecommendedNews> newNewsList = generateNewsForKeyword(trend.getTitle());
                if (newNewsList.isEmpty()) continue;

                for (RecommendedNews news : newNewsList) {
                    trend.addRecommendedNews(news);
                }

                // 점수 갱신
                trend.updateNewsScore(newNewsList.get(0).getScore());

            } catch (Exception e) {
                log.error("트렌드 뉴스 갱신 실패 - 트렌드 ID: {}", trend.getId(), e);
                throw new AppException(TREND_NEWS_REFRESH_FAIL);
            }
        }

        log.info("모든 트렌드 뉴스 및 뉴스 점수 갱신 완료");
    }
}
