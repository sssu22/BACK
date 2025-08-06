package com.example.trendlog.service;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.dto.request.trend.SimilarTrendRequest;
import com.example.trendlog.dto.response.trend.SimilarTrendIdsResponse;
import com.example.trendlog.repository.trend.TrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimilarTrendService {
    private final TrendRepository trendRepository;
    private final WebClient webClient = WebClient.create("http://localhost:8000");

    // 1. 비슷한 트렌드 추천 → 저장
    public List<Trend> getSimilarTrends(Trend trend, String title, String description, String category) {
        // 1-1. FastAPI에 추천 요청
        SimilarTrendRequest request = new SimilarTrendRequest(
                title,
                description,
                category
        );

        SimilarTrendIdsResponse response = webClient.post()
                .uri("/find-similar-trends")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SimilarTrendIdsResponse.class)
                .block();

        // 1-2. 추천 ID에서 Trend 엔티티 조회 (자기 자신은 제외)
        List<Long> similarIds = response != null ? response.getSimilar_trend_ids() : List.of();

        List<Trend> similarTrends = trendRepository.findAllById(similarIds)
                .stream()
                .filter(t -> !t.getId().equals(trend.getId())) // 자기 자신 제외
                .toList();

        return similarTrends;
    }
}
