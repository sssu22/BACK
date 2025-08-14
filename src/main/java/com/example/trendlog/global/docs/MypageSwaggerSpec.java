package com.example.trendlog.global.docs;

import com.example.trendlog.dto.response.mypage.ProfileResponse;
import com.example.trendlog.dto.response.mypage.RecentPostActivityResponse;
import com.example.trendlog.dto.response.mypage.UserStatisticsResponse;
import com.example.trendlog.dto.response.post.PostListResponse;
import com.example.trendlog.dto.response.trend.TrendListResponse;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.global.dto.ErrorResponse;
import com.example.trendlog.global.security.userdetails.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Mypage", description = "마이페이지 관련 API")
public interface MypageSwaggerSpec {
    @Operation(summary = "내 활동 통계 요약", description = "총 경험수, 평균 트렌드 점수, 방문지역 수(구 기준), 스크랩 수을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 활동 통계 요약 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    public ResponseEntity<DataResponse<UserStatisticsResponse>> getUserStatistics(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "내 최근 활동", description = "내가 쓴 게시글 최근 3개를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 최근 활동 게시글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<DataResponse<List<RecentPostActivityResponse>>> getRecentPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "내가 스크랩한 게시글", description = "내가 스크랩한 게시글 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내가 스크랩한 게시글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<DataResponse<List<PostListResponse>>> getPosts(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "내가 스크랩한 트렌드", description = "내가 스크랩한 트렌드 목록을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내가 스크랩한 트렌드 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<DataResponse<List<TrendListResponse>>> getTrends(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );

    @Operation(summary = "마이페이지 상단 프로필 정보", description = "마이페이지 상단 프로필 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "마이페이지 상단 프로필 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public ResponseEntity<DataResponse<ProfileResponse>> getProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails
    );
}
