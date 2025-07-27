package com.example.trendlog.global.docs;

import com.example.trendlog.dto.response.post.PostPagedResponse;
import com.example.trendlog.dto.response.trend.TrendSearchPagedResponse;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.global.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Tag(name = "Search", description = "검색 관련 API")
public interface SearchSwaggerSpec {
    // 게시글 검색
    @Operation(summary = "게시글 검색", description = "키워드, 감정, 정렬(latest/trend) 조건으로 게시글을 검색합니다.\n " +
            "현재 존재하는 감정은 JOY, EXCITEMENT, NOSTALGIA, SURPRISE, LOVE 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 검색 성공")
    })
    public ResponseEntity<DataResponse<PostPagedResponse>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String emotion,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy
    );

    // 내 게시글 검색
    @Operation(summary = "내 게시글 검색", description = "내가 작성한 게시글을 키워드, 감정, 정렬(latest/trend) 조건으로 검색합니다.\n " +
            "현재 존재하는 감정은 JOY, EXCITEMENT, NOSTALGIA, SURPRISE, LOVE 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 게시글 검색 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<PostPagedResponse>> searchMyPosts(
            Principal principal,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String emotion,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy
    );

    // 내가 스크랩한 게시글 검색
    @Operation(summary = "내가 스크랩한 게시글 검색", description = "내가 스크랩한 게시글을 키워드, 감정, 정렬(latest/trend) 조건으로 검색합니다.\n " +
            "현재 존재하는 감정은 JOY, EXCITEMENT, NOSTALGIA, SURPRISE, LOVE 입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내가 스크랩한 게시글 검색 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<PostPagedResponse>> searchScrappedPosts(
            Principal principal,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String emotion,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy
    );

    // 트렌드 검색
    @Operation(summary = "트렌드 검색", description = "트렌드를 키워드, 카테고리, 정렬(latest/trend) 조건으로 검색합니다.\n " +
            "현재 존재하는 카테고리는 FOOD, LIFESTYLE, CULTURE, HEALTH, INVESTMENT, SOCIAL, ETC입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트렌드 검색 성공")
    })
    public ResponseEntity<DataResponse<TrendSearchPagedResponse>> searchTrends(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy // trend, latest
    );

    // 내가 스크랩한 트렌드 검색
    @Operation(summary = "내가 스크랩한 트렌드 검색", description = "내가 스크랩한 트렌드를 키워드, 카테고리, 정렬(latest/trend) 조건으로 검색합니다.\n " +
            "현재 존재하는 카테고리는 FOOD, LIFESTYLE, CULTURE, HEALTH, INVESTMENT, SOCIAL, ETC입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내가 스크랩한 트렌드 검색 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<DataResponse<TrendSearchPagedResponse>> searchScrappedTrends(
            Principal principal,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy // trend, latest
    );

}
