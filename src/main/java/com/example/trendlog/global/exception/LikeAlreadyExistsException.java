package com.example.trendlog.global.exception;

public class LikeAlreadyExistsException extends AppException {
    public LikeAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
}
