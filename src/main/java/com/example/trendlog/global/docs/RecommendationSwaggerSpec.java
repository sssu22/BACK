package com.example.trendlog.global.docs;

import com.example.trendlog.dto.request.trend.RecommendSaveRequest;
import com.example.trendlog.dto.response.trend.RecommendedTrendResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Tag(name = "Recommendations", description = "사용자 맞춤 트렌드 추천")
public interface RecommendationSwaggerSpec {
    @Operation(summary = "트렌드 추천 조회", description = "인증된 사용자 한정, 사용자 맞춤 트렌드 추천 목록 3개를 조회합니다. ")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자(USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<DataResponse<List<RecommendedTrendResponse>>> getRecommendations(
            @AuthenticationPrincipal UserDetailsImpl userDetails);

//    @Operation(summary = "트렌드 추천 저장(백엔드 파이썬 내부 호출용)", description = "userId와 trendId를 사용하여 사용자별 추천 트렌드를 저장합니다.")
//    @ApiResponses({
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 (USER-001)/ 존재하지 않은 트렌드(TREND-002)",
//                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    @PostMapping
//    public ResponseEntity<DataResponse<Void>> createRecommendation(@RequestBody RecommendSaveRequest request);

}
