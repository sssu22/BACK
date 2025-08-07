package com.example.trendlog.service;

import com.example.trendlog.dto.request.trend.TrendTagRequest;
import com.example.trendlog.dto.response.trend.TrendTagResponse;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.PythonErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagGenerationService {
    private final WebClient webClient=WebClient.create("http://localhost:8000");

    public List<String> generateTags(String title, String description){
        try{
            TrendTagRequest request = new TrendTagRequest(title, description);

            TrendTagResponse response = webClient.post()
                    .uri("/generate-tags")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TrendTagResponse.class)
                    .block();
            if (response == null) {
                throw new AppException(PythonErrorCode.PYTHON_TAG_GENERATE_FAIL); //PYTHON-004
            }
            return response.getTags();
        }catch(Exception e){
            throw new AppException(PythonErrorCode.PYTHON_EXEC_FAIL); //PYTHON-001
        }
    }
}
