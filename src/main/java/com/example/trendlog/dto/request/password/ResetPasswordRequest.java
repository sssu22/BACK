package com.example.trendlog.dto.request.password;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ResetPasswordRequest {
    @NotBlank(message = "토큰은 필수입니다.")
    private String token;
    @NotBlank(message = "새로운 비밀번호는 필수입니다.")
    private String newPassword;
}
