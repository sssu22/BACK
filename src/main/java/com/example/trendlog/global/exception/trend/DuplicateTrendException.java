package com.example.trendlog.global.exception.trend;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.TrendErrorCode;

public class DuplicateTrendException extends AppException {
    public DuplicateTrendException() {
        super(TrendErrorCode.DUPLICATE_TREND);
    }
}
