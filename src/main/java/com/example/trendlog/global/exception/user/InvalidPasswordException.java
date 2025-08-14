package com.example.trendlog.global.exception.user;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.UserErrorCode;

public class InvalidPasswordException extends AppException {
    public InvalidPasswordException() {
        super(UserErrorCode.INVALID_PASSWORD);
    }
}
