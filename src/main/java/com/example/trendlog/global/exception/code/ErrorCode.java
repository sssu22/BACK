package com.example.trendlog.global.exception.code;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getHttpStatus();
    String getMessage();
    String getCode();
}
