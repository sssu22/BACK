package com.example.trendlog.global.exception.user;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.UserErrorCode;

public class EmailAlreadyExistsException extends AppException {
    public EmailAlreadyExistsException() {
        super(UserErrorCode.EMAIL_ALREADY_EXISTS);
    }
}
