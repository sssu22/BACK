package com.example.trendlog.global.exception;

import com.example.trendlog.global.exception.code.ErrorCode;

public class LikeAlreadyExistsException extends AppException {
    public LikeAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }
//    public LikeAlreadyExistsException() {
//            super();
//        }

}
