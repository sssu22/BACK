package com.example.trendlog.global.exception.trend;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.TrendErrorCode;

public class InvalidCategoryException extends AppException {
    public InvalidCategoryException() {
        super(TrendErrorCode.INVALID_CATEGORY);
    }
}
