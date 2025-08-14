package com.example.trendlog.global.security.jwt;

import com.example.trendlog.global.dto.ErrorResponse;
import com.example.trendlog.global.exception.code.UserErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
//    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType("application/json");
//
//        ErrorResponse errorResponse = ErrorResponse.of(UserErrorCode.UNAUTHORIZED_USER, request);
//        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
            {
              "code": "USER-011",
              "message": "인증되지 않은 사용자입니다."
            }
        """);
    }
}
