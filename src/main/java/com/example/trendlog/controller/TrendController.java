package com.example.trendlog.controller;

import com.example.trendlog.dto.request.trend.TrendCommentCreateRequest;
import com.example.trendlog.dto.request.trend.TrendCreateRequest;
import com.example.trendlog.dto.response.trend.*;
import com.example.trendlog.global.docs.TrendSwaggerSpec;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.global.security.userdetails.UserDetailsImpl;
import com.example.trendlog.service.TrendService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/trends")
@RequiredArgsConstructor
public class TrendController implements TrendSwaggerSpec {
    private final TrendService trendService;
    @PostMapping
    public ResponseEntity<DataResponse<TrendCreateResponse>> createTrend(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody TrendCreateRequest request
    ){
        TrendCreateResponse response = trendService.createTrend(userDetails.getUserId(), request);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @GetMapping
    public ResponseEntity<DataResponse<TrendListPageResponse>> getTrendList(
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable){
        TrendListPageResponse response = trendService.getTrendList(pageable);
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @GetMapping("/{trendId}")
    public ResponseEntity<DataResponse<TrendDetailResponse>> getTrendDetail(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long trendId){
        TrendDetailResponse response=trendService.getTrendDetail(userDetails.getUser(),trendId);
        return ResponseEntity.ok(DataResponse.from(response));
    }
    @PostMapping("/{trendId}/comments")
    public ResponseEntity<DataResponse<Void>> createTrendComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long trendId,
            @Valid @RequestBody TrendCommentCreateRequest request){
        trendService.addComment(trendId,userDetails.getUserId(), request.getContent());
        return ResponseEntity.ok(DataResponse.ok());
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<DataResponse<Void>> deleteTrendComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId){
        trendService.deleteComment(commentId,userDetails.getUserId());
        return ResponseEntity.ok(DataResponse.ok());
    }

    @PostMapping("/{trendId}/like")
    public ResponseEntity<DataResponse<Void>> likeTrend(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long trendId){
        trendService.likeTrend(trendId,userDetails.getUserId());
        return ResponseEntity.ok(DataResponse.ok());
    }

    @PostMapping("/{trendId}/scrap")
    public ResponseEntity<DataResponse<Void>> scrapTrend(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long trendId
    ){
        trendService.scrapTrend(trendId,userDetails.getUserId());
        return ResponseEntity.ok(DataResponse.ok());
    }

    @GetMapping("/popular")
    public ResponseEntity<DataResponse<List<PopularTrendResponse>>> getPopularTrend(){
        List<PopularTrendResponse> responses=trendService.getPopularTrends();
        return ResponseEntity.ok(DataResponse.from(responses));
    }

    @GetMapping("/recent")
    public ResponseEntity<DataResponse<List<RecentTrendResponse>>> getRecentTrend(){
        List<RecentTrendResponse> responses=trendService.getRecentTrends();
        return ResponseEntity.ok(DataResponse.from(responses));
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<DataResponse<Void>> likeTrendComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId){
        trendService.likeComment(commentId,userDetails.getUserId());
        return ResponseEntity.ok(DataResponse.ok());
    }


}
