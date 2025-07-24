package com.example.trendlog.global.docs;

import com.example.trendlog.dto.request.trend.TrendCommentCreateRequest;
import com.example.trendlog.dto.request.trend.TrendCreateRequest;
import com.example.trendlog.dto.response.trend.*;
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
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Trends", description = "트렌드 관련 API")
public interface TrendSwaggerSpec {

    @Operation(summary = "트렌드 생성", description = "이름, 설명, 카테고리로 트렌드를 생성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트렌드 생성 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 트렌드 (TREND-409)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 트렌드 카테고리 (TREND-010)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<TrendCreateResponse>> createTrend(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody TrendCreateRequest request
    );

    @Operation(summary = "트렌드 목록 조회", description = "트렌드 목록을 페이징을 통해 조회합니다.\\n\\n예시: `/api/v1/trends?page=0&size=10&sort=createdAt,desc`")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트렌드 목록 조회 성공"),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 페이지 요청 (TREND-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            })
    public ResponseEntity<DataResponse<TrendListPageResponse>> getTrendList(Pageable pageable);

    @Operation(summary = "트렌드 상세 조회", description = "트렌드의 내용을 상세 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트렌드 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 트렌드 (TREND-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 트렌드 카테고리 (TREND-010)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<TrendDetailResponse>> getTrendDetail(@PathVariable Long trendId);

    @Operation(summary = "트렌드 댓글 작성", description = "트렌드의 댓글을 생성합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트렌드 댓글 생성 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/ 존재하지 않는 트렌드 (TREND-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<Void>> createTrendComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long trendId,
            @Valid @RequestBody TrendCommentCreateRequest request);

    @Operation(summary = "트렌드 댓글 삭제", description = "트렌드 댓글을 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "트렌드 댓글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 댓글이 존재하지 않음 (TREND-007)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음(USER-012)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<Void>> deleteTrendComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId);

    @Operation(summary = "트렌드 좋아요", description = "좋아요를 누르거나 취소합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요/좋아요 취소 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/ 존재하지 않는 트렌드 (TREND-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<Void>> likeTrend(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long trendId);

    @Operation(summary = "트렌드 스크랩", description = "스크랩을 누르거나 취소합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "스크랩/스크랩 취소 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/ 존재하지 않는 트렌드 (TREND-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<Void>> scrapTrend(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long trendId
    );

    @Operation(summary = "최고 트렌드 목록", description = "최고 트렌드 목록 점수 top5를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최고 트렌드 목록 조회 성공"),
            })
    public ResponseEntity<DataResponse<List<PopularTrendResponse>>> getPopularTrend();

    @Operation(summary = "최근 트렌드 목록", description = "최근 트렌드 목록 점수 상승률 top3를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "최근 트렌드 목록 조회 성공"),
    })
    public ResponseEntity<DataResponse<List<RecentTrendResponse>>> getRecentTrend();

    @Operation(summary = "트렌드 댓글 좋아요", description = "댓글 좋아요를 누르거나 취소합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요/좋아요 취소 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/ 존재하지 않는 트렌드 댓글(TREND-007)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<Void>> likeTrendComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId);
}