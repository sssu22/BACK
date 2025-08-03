package com.example.trendlog.controller;

import com.example.trendlog.domain.User;
import com.example.trendlog.dto.request.trend.RecommendSaveRequest;
import com.example.trendlog.dto.response.trend.RecommendedTrendResponse;
import com.example.trendlog.global.docs.RecommendationSwaggerSpec;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.global.security.userdetails.UserDetailsImpl;
import com.example.trendlog.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommend")
@RequiredArgsConstructor
public class RecommendationController implements RecommendationSwaggerSpec{
    private final RecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<DataResponse<List<RecommendedTrendResponse>>> getRecommendations(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        User user = userDetails != null ? userDetails.getUser() : null;
        List<RecommendedTrendResponse> responses = recommendationService.getRecommendations(user);
        return ResponseEntity.ok(DataResponse.from(responses));
    }
//API 스타일로 할 경우를 생각해서 남겨놓음
//    @PostMapping
//    public ResponseEntity<DataResponse<Void>> createRecommendation(@RequestBody RecommendSaveRequest request){
//        recommendationService.saveRecommendations(request.getUserId(),request.getTrendIds());
//        return ResponseEntity.ok(DataResponse.ok());
//    }
}
