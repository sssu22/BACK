package com.example.trendlog.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PythonErrorCode implements ErrorCode{
    PYTHON_EXEC_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "Python 스크립트 실행에 실패했습니다.", "PYTHON-001"),
    PYTHON_BAD_REQUEST(HttpStatus.BAD_REQUEST, "Python 서비스에 잘못된 요청입니다.", "PYTHON-002"),
    PYTHON_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Python 서비스 연결 실패", "PYTHON-003"),
    PYTHON_TAG_GENERATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "Python 태그 생성기 응답이 비어있습니다.", "PYTHON-004");
    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
