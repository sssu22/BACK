package com.example.trendlog.global.docs;

import com.example.trendlog.dto.request.auth.TokenRefreshRequest;
import com.example.trendlog.dto.request.auth.UserLoginRequest;
import com.example.trendlog.dto.request.auth.UserSignupRequest;
import com.example.trendlog.dto.response.auth.LoginResponse;
import com.example.trendlog.dto.response.auth.TokenRefreshResponse;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.global.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
@Tag(name = "Auth", description = "인증 관련 API")
public interface AuthApiDoc {
    @Operation(summary = "회원가입", description = "이메일, 비밀번호를 이용해 회원가입을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 이메일 (USER-004)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<Void>> signup(@Valid @RequestBody UserSignupRequest request);

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인 후 JWT 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 로그인 정보 (USER-002)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 (COMMON-005)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<LoginResponse>> login(@Valid @RequestBody UserLoginRequest request);


    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 Access Token을 재발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰 (USER-010)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 (COMMON-005)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<TokenRefreshResponse>> refresh(@Valid @RequestBody TokenRefreshRequest request);

    @Operation(summary = "로그아웃", description = "현재 로그인한 사용자의 리프레시 토큰을 삭제합니다.")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 (USER-011)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    ResponseEntity<DataResponse<Void>> logout(@Parameter(hidden = true) @AuthenticationPrincipal Authentication authentication);


}
