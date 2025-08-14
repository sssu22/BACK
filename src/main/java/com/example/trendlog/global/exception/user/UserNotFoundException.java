package com.example.trendlog.global.exception.user;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.UserErrorCode;

public class UserNotFoundException extends AppException {
    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
