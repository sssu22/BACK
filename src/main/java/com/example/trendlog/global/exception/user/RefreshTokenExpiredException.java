package com.example.trendlog.global.exception.user;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.UserErrorCode;
import jdk.jshell.spi.ExecutionControl;

public class RefreshTokenExpiredException extends AppException {
    public RefreshTokenExpiredException() {
        super(UserErrorCode.REFRESH_TOKEN_EXPIRED);
    }
}
