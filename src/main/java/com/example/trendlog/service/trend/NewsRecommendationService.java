package com.example.trendlog.service.trend;

import com.example.trendlog.domain.trend.RecommendedNews;
import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.dto.response.trend.NewsItemDto;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.repository.trend.TrendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

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
    private final WebClient fastApiWebClient;

    public List<RecommendedNews> generateNewsForKeyword(String keyword) {
        try {
            // FastAPI 호출
            List<NewsItemDto> items = fastApiWebClient.post()
                    .uri("/jobs/news/generate")
                    .body(BodyInserters.fromValue(new NewsGenerateRequest(keyword)))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<NewsItemDto>>() {})
                    .block();

            List<RecommendedNews> result = new ArrayList<>();
            if (items != null) {
                for (NewsItemDto dto : items) {
                    RecommendedNews news = RecommendedNews.builder()
                            .keyword(dto.getKeyword())
                            .title(dto.getTitle())
                            .link(dto.getLink())
                            .score(dto.getScore())
                            .build();
                    result.add(news);
                }
            }
            return result;

        } catch (Exception e) {
            log.error("뉴스 생성 실패 (keyword={})", keyword, e);
            throw new AppException(PYTHON_EXEC_FAIL);
        }
    }

    // 매주 월요일 00:00
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    @Transactional
    public void refreshNewsAndScore() {
        List<Trend> trends = trendRepository.findAll();
        for (Trend trend : trends) {
            try {
                trend.clearRecommendedNews();

                List<RecommendedNews> list = generateNewsForKeyword(trend.getTitle());
                if (list.isEmpty()) continue;

                list.forEach(trend::addRecommendedNews);
                trend.updateNewsScore(list.get(0).getScore());

            } catch (Exception e) {
                log.error("트렌드 뉴스 갱신 실패 - 트렌드 ID: {}", trend.getId(), e);
                throw new AppException(TREND_NEWS_REFRESH_FAIL);
            }
        }
        log.info("모든 트렌드 뉴스 및 점수 갱신 완료");
    }

    // 요청 바디용 내부 클래스
    private record NewsGenerateRequest(String keyword) {}
}
