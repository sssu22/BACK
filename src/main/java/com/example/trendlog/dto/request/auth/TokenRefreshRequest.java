package com.example.trendlog.dto.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TokenRefreshRequest {

    @NotBlank(message = "Refresh Token은 필수입니다.")
    private String refreshToken;

}
