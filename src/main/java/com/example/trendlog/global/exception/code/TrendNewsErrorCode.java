package com.example.trendlog.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TrendNewsErrorCode implements ErrorCode{
    PYTHON_EXEC_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "Python 스크립트 실행에 실패했습니다.", "NEWS-001"),
    CSV_READ_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "추천 뉴스 CSV 파일을 읽는 데 실패했습니다.", "NEWS-002"),
    CSV_PARSE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "CSV 데이터를 파싱하는 중 오류가 발생했습니다.", "NEWS-003"),
    TREND_NEWS_REFRESH_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "트렌드 뉴스 및 점수 갱신 중 오류가 발생했습니다.", "NEWS-004");
    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
