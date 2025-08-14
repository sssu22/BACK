package com.example.trendlog.global.exception.user;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.UserErrorCode;

public class InvalidRefreshTokenException extends AppException {
    public InvalidRefreshTokenException() {
        super(UserErrorCode.INVALID_REFRESH_TOKEN);
    }
}
