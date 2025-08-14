package com.example.trendlog.dto.request.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ForgotPasswordRequest {
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;
}
