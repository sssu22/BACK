package com.example.trendlog.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TrendErrorCode implements ErrorCode {
    DUPLICATE_TREND(HttpStatus.CONFLICT, "이미 존재하는 트렌드입니다.", "TREND-001"),
    TREND_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 트렌드입니다.", "TREND-002"),
    ANALYSIS_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 트렌드의 분석 정보가 존재하지 않습니다.", "TREND-003"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 트렌드에 연결된 게시글이 없습니다.", "TREND-004"),
    COMMENT_TREND_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 작성할 트렌드를 찾을 수 없습니다.", "TREND-005"),
    UNAUTHORIZED_COMMENT_DELETE(HttpStatus.FORBIDDEN, "해당 댓글을 삭제할 권한이 없습니다.", "TREND-006"),
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "삭제할 댓글이 존재하지 않습니다.", "TREND-007"),
    LIKE_TREND_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 대상 트렌드가 존재하지 않습니다.", "TREND-008"),
    SCRAP_TREND_NOT_FOUND(HttpStatus.NOT_FOUND, "스크랩 대상 트렌드가 존재하지 않습니다.", "TREND-009");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
