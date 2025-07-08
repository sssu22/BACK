package com.example.trendlog.global.exception.user;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.UserErrorCode;

public class InvalidLoginInfoException extends AppException {
    public InvalidLoginInfoException() {
        super(UserErrorCode.INVALID_LOGIN_INFO);
    }
}
