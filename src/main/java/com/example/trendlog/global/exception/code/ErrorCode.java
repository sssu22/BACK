package com.example.trendlog.global.exception.code;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getHttpStatus();
    String getMessage();
    String getCode();
    default ErrorCode withDetail(String detailMessage) {
        return new ErrorCode() {
            public HttpStatus getHttpStatus() {
                return ErrorCode.this.getHttpStatus();
            }

            public String getMessage() {
                return detailMessage;
            }

            public String getCode() {
                return ErrorCode.this.getCode();
            }
        };
    }
}
