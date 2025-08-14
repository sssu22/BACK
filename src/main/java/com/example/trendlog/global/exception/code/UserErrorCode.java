package com.example.trendlog.global.exception.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다.", "USER-001"),
    INVALID_LOGIN_INFO(HttpStatus.BAD_REQUEST, "잘못된 로그인 정보입니다.", "USER-002"),
    UNAUTHORIZED_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 정보입니다.", "USER-003"),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 가입된 이메일입니다.", "USER-004"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "비밀번호가 틀렸습니다.", "USER-005"),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다.", "USER-006"),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임입니다.", "USER-007"),
    ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "액세스 토큰이 만료되었습니다.", "USER-008"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 만료되었습니다.", "USER-009"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다.", "USER-010"),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다.", "USER-011"),
    USER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", "USER-012"),
    ALREADY_DELETED_USER(HttpStatus.BAD_REQUEST, "이미 탈퇴한 사용자입니다.", "USER-013"),
    INVALID_PROFILE_UPDATE(HttpStatus.BAD_REQUEST, "유효하지 않은 사용자 수정 요청입니다.", "USER-014"),
    SAME_AS_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일합니다.", "USER-015"),
    UPLOAD_PROFILE_IMAGE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "프로필 이미지 업로드에 실패했습니다.", "USER-016"),
    PASSWORD_RESET_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "비밀번호 재설정 토큰이 만료되었거나 유효하지 않습니다.", "USER-017");

    private final HttpStatus httpStatus;
    private final String message;
    private final String code;
}
