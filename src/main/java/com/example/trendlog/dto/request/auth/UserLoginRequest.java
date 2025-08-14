package com.example.trendlog.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserLoginRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
