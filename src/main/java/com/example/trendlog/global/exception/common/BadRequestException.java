package com.example.trendlog.global.exception.common;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.CommonErrorCode;

public class BadRequestException extends AppException {
    public BadRequestException(String message) {
        super(CommonErrorCode.BAD_REQUEST);
    }
}
