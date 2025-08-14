package com.example.trendlog.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class PasswordChangeRequest {
    @NotBlank(message = "기존 비밀번호는 필수입니다.")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호는 필수입니다.")
    private String newPassword;
}
