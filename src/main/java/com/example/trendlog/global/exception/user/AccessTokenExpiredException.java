package com.example.trendlog.global.exception.user;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.UserErrorCode;

public class AccessTokenExpiredException extends AppException {
    public AccessTokenExpiredException() {
        super(UserErrorCode.ACCESS_TOKEN_EXPIRED);
    }
}
