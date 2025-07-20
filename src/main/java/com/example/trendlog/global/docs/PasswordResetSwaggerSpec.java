package com.example.trendlog.global.docs;

import com.example.trendlog.dto.request.password.ForgotPasswordRequest;
import com.example.trendlog.dto.request.password.ResetPasswordRequest;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.global.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "PasswordReset", description = "비밀번호 재발급(비밀번호를 잊었을 때)")
public interface PasswordResetSwaggerSpec {
    @Operation(summary = "비밀번호 재설정 메일", description = "이메일을 입력해, 메일로 토큰이 포함된 비밀번호 재설정 링크를 전송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "이메일 전송 실패 (MAIL-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<String>> sendResetEmail(@RequestBody @Valid ForgotPasswordRequest request);

    @Operation(summary = "비밀번호 재설정", description = "임시 토큰을 사용해 비밀번호를 재설정 합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "존재하지 않는 회원 (USER-001)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "비밀번호 재설정 토큰 만료&유효하지 않음 (USER-017)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<DataResponse<String>> resetPassword(@RequestBody @Valid ResetPasswordRequest request);
    }



