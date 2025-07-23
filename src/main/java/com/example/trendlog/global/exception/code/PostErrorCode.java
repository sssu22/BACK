package com.example.trendlog.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PostErrorCode implements ErrorCode {

    EMOTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 감정입니다.", "POST-001"),
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다.", "POST-002"),
    POST_NOT_WRITER(HttpStatus.FORBIDDEN, "작성자가 아닙니다.", "POST-003"),
    POST_EMPTY_FIELD(HttpStatus.BAD_REQUEST, "게시글 작성 시 빈 값이 있습니다.", "POST-004"),
    POST_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다.", "POST-005"),
    ;
    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}

