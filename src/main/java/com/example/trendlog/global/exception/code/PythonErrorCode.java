package com.example.trendlog.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PythonErrorCode implements ErrorCode{
    PYTHON_EXEC_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "Python 스크립트 실행에 실패했습니다.", "PYTHON-001");
    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
