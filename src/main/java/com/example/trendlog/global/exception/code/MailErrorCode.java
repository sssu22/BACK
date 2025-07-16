package com.example.trendlog.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MailErrorCode implements ErrorCode{
    MAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 전송에 실패했습니다.", "MAIL-001"),
    MAIL_TEMPLATE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 템플릿 렌더링에 실패했습니다.", "MAIL-002"),
    INVALID_EMAIL_ADDRESS(HttpStatus.BAD_REQUEST, "유효하지 않은 이메일 주소입니다.", "MAIL-003");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
