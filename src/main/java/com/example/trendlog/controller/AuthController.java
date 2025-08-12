package com.example.trendlog.controller;

import com.example.trendlog.dto.request.auth.TokenRefreshRequest;
import com.example.trendlog.dto.request.auth.UserLoginRequest;
import com.example.trendlog.dto.request.auth.UserSignupRequest;
import com.example.trendlog.dto.response.auth.LoginResponse;
import com.example.trendlog.dto.response.auth.TokenRefreshResponse;
import com.example.trendlog.global.docs.AuthSwaggerSpec;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.service.user.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController implements AuthSwaggerSpec {
    private final AuthService authService;
    /**
     * 회원가입
     */
    @PostMapping("/signup")
    public ResponseEntity<DataResponse<Void>> signup(@Valid @RequestBody UserSignupRequest request){
        authService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(DataResponse.ok());
    }

    /**
     * 로그인
     */
    @PostMapping("/login")
    public ResponseEntity<DataResponse<LoginResponse>> login(@Valid @RequestBody UserLoginRequest request){
        LoginResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(DataResponse.from(loginResponse));
    }

    /**
     * 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<DataResponse<Void>> logout(Authentication authentication){
        String email=authentication.getName();
        authService.logout(email);
        return ResponseEntity.ok(DataResponse.ok());
    }

    /**
     * 토큰 재발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<DataResponse<TokenRefreshResponse>> refresh(@Valid @RequestBody TokenRefreshRequest request){
        TokenRefreshResponse response = authService.refresh(request);
        return ResponseEntity.ok(DataResponse.from(response));
    }
}
