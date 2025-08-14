package com.example.trendlog.global.exception.code;

import com.example.trendlog.global.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
    UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다.", "FILE-001"),
    DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제에 실패했습니다.", "FILE-002"),
    COPY_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 복사에 실패했습니다.", "FILE-003"),
    DOWNLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "파일 다운로드에 실패했습니다.", "FILE-004");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
