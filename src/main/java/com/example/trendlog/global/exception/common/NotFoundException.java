package com.example.trendlog.global.exception.common;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.CommonErrorCode;

public class NotFoundException extends AppException {
    public NotFoundException() {
        super(CommonErrorCode.NOT_FOUND);
    }
}
