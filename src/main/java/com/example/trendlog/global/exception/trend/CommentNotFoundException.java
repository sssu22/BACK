package com.example.trendlog.global.exception.trend;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.TrendErrorCode;

public class CommentNotFoundException extends AppException {
    public CommentNotFoundException() {
        super(TrendErrorCode.COMMENT_NOT_FOUND);
    }
}
