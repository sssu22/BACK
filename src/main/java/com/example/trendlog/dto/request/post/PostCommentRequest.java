package com.example.trendlog.dto.request.post;

import com.example.trendlog.global.exception.AppException;

import static com.example.trendlog.global.exception.code.PostErrorCode.POST_EMPTY_FIELD;

public record PostCommentRequest(
        String content
) {
    public void validate() {
        if (content == null || content.isBlank()) {
            throw new AppException(POST_EMPTY_FIELD);
        }
    }
}
