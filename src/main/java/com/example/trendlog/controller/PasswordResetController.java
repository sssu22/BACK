package com.example.trendlog.controller;

import com.example.trendlog.dto.request.password.ForgotPasswordRequest;
import com.example.trendlog.dto.request.password.ResetPasswordRequest;
import com.example.trendlog.global.docs.PasswordResetSwaggerSpec;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.service.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/password")
public class PasswordResetController implements PasswordResetSwaggerSpec {
    private final PasswordResetService passwordResetService;

    @PostMapping("/reset-request")
    public ResponseEntity<DataResponse<String>> sendResetEmail(@RequestBody @Valid ForgotPasswordRequest request){
        passwordResetService.sendResetEmail(request.getEmail());
        return ResponseEntity.ok(DataResponse.from("비밀번호 재설정 메일이 발송되었습니다."));
    }

    @PostMapping("/reset")
    public ResponseEntity<DataResponse<String>> resetPassword(@RequestBody @Valid ResetPasswordRequest request){
        passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(DataResponse.from("비밀번호가 성공적으로 재설정되었습니다."));
    }
}
