package com.example.trendlog.service;

import com.example.trendlog.dto.request.trend.TrendTagRequest;
import com.example.trendlog.dto.response.trend.TrendTagResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class TagGenerationService {
    private final WebClient webClient=WebClient.create("http://localhost:8000");

    public List<String> generateTags(String title, String description){
        TrendTagRequest request = new TrendTagRequest(title, description);

        TrendTagResponse response = webClient.post()
                .uri("/generate-tags")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(TrendTagResponse.class)
                .block();
        return response !=null ? response.getTags() : List.of();
    }
}
