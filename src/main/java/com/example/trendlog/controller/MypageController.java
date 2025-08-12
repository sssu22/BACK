package com.example.trendlog.controller;

import com.example.trendlog.dto.response.mypage.ProfileResponse;
import com.example.trendlog.dto.response.mypage.RecentPostActivityResponse;
import com.example.trendlog.dto.response.mypage.UserStatisticsResponse;
import com.example.trendlog.dto.response.post.PostListResponse;
import com.example.trendlog.dto.response.trend.TrendListResponse;
import com.example.trendlog.global.docs.MypageSwaggerSpec;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.global.security.userdetails.UserDetailsImpl;
import com.example.trendlog.service.user.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class MypageController implements MypageSwaggerSpec {
    private final MypageService mypageService;

    @GetMapping("/stats")
    public ResponseEntity<DataResponse<UserStatisticsResponse>> getUserStatistics(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        UserStatisticsResponse response = mypageService.getUserStatistics(userDetails.getUserId());
        return ResponseEntity.ok(DataResponse.from(response));
    }

    @GetMapping("/posts/recent")
    public ResponseEntity<DataResponse<List<RecentPostActivityResponse>>> getRecentPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        List<RecentPostActivityResponse> responses=mypageService.getRecentPostActivity(userDetails.getUserId());
        return ResponseEntity.ok(DataResponse.from(responses));
    }

    @GetMapping("/scraps")
    public ResponseEntity<DataResponse<List<PostListResponse>>> getPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        List<PostListResponse> responses = mypageService.getMyScrapPostList(userDetails.getUserId());
        return ResponseEntity.ok(DataResponse.from(responses));
    }

    @GetMapping("/trends")
    public ResponseEntity<DataResponse<List<TrendListResponse>>> getTrends(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        List<TrendListResponse> responses = mypageService.getMyScrapTrendList(userDetails.getUserId());
        return ResponseEntity.ok(DataResponse.from(responses));
    }

    @GetMapping("/profile")
    public ResponseEntity<DataResponse<ProfileResponse>> getProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
        ProfileResponse response = mypageService.getMyProfile(userDetails.getUserId());
        return ResponseEntity.ok(DataResponse.from(response));
    }

}
