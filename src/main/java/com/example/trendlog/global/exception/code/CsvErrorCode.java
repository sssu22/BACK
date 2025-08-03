package com.example.trendlog.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CsvErrorCode implements ErrorCode{
    SCORE_EXPORT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "추천 점수 CSV 저장에 실패했습니다.", "CSV-001"),
    TREND_EXPORT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "트렌드 정보 CSV 저장에 실패했습니다.", "CSV-002"),
    RECOMMEND_IMPORT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "추천 결과 CSV 읽기에 실패했습니다.", "CSV-003");
    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
