package com.example.trendlog.global.security.jwt;

import com.example.trendlog.global.dto.ErrorResponse;
import com.example.trendlog.global.exception.code.UserErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
//    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        response.setContentType("application/json");
//
//        ErrorResponse errorResponse = ErrorResponse.of(UserErrorCode.USER_ACCESS_DENIED, request);
//        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("""
            {
              "code": "USER-012",
              "message": "접근 권한이 없습니다."
            }
        """);
    }
}
