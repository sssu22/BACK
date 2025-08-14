package com.example.trendlog.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum GoogleErrorCode implements ErrorCode{
    YOUTUBE_API_REQUEST_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "유튜브 API 요청에 실패했습니다.", "GOOGLE-001"),
    YOUTUBE_API_RESPONSE_ERROR(HttpStatus.BAD_GATEWAY, "유튜브 API 응답 오류.", "GOOGLE-002"),
    YOUTUBE_API_KEY_INVALID(HttpStatus.FORBIDDEN, "유효하지 않은 유튜브 API 키.", "GOOGLE-003"),
    YOUTUBE_API_QUOTA_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "유튜브 API 쿼터 초과.", "GOOGLE-004"),
    YOUTUBE_RESULT_EMPTY(HttpStatus.NOT_FOUND, "검색 결과가 없습니다.", "GOOGLE-005");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
