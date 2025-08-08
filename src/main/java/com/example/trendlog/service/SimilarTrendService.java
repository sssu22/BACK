package com.example.trendlog.service;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.dto.request.trend.SimilarTrendRequest;
import com.example.trendlog.dto.response.trend.SimilarTrendIdsResponse;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.PythonErrorCode;
import com.example.trendlog.repository.trend.TrendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SimilarTrendService {
    private final TrendRepository trendRepository;
    private final WebClient webClient = WebClient.create("http://localhost:8000");

    public List<Trend> getSimilarTrends(Trend trend, String title, String description, String category) {
        SimilarTrendRequest request = new SimilarTrendRequest(
                title,
                description,
                category
        );
        try{
            SimilarTrendIdsResponse response = webClient.post()
                    .uri("/find-similar-trends")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(SimilarTrendIdsResponse.class)
                    .block();
            if(response==null){
                throw new AppException(PythonErrorCode.PYTHON_EXEC_FAIL); //PYTHON-001
            }
            List<Long> similarIds = response != null ? response.getSimilar_trend_ids() : List.of();

            return trendRepository.findAllById(similarIds)
                    .stream()
                    .filter(t -> !t.getId().equals(trend.getId())) // 자기 자신 제외
                    .toList();
        }catch (WebClientResponseException e){
            throw new AppException(PythonErrorCode.PYTHON_EXEC_FAIL); //PYTHON-001
        }catch (Exception e){
            throw new AppException(PythonErrorCode.PYTHON_SERVICE_UNAVAILABLE); //PYTHON-003
        }
    }
}
