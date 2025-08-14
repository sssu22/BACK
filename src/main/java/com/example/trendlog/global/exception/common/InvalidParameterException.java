package com.example.trendlog.global.exception.common;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.CommonErrorCode;

public class InvalidParameterException extends AppException {
    public InvalidParameterException(String message) {
        super(CommonErrorCode.INVALID_PARAMETER);
    }
}
