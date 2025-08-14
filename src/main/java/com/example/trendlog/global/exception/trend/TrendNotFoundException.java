package com.example.trendlog.global.exception.trend;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.TrendErrorCode;

public class TrendNotFoundException extends AppException {
    public TrendNotFoundException() {
        super(TrendErrorCode.TREND_NOT_FOUND);
    }
}
