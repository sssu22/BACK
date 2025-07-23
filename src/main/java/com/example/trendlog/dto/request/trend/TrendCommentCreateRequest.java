package com.example.trendlog.dto.request.trend;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TrendCommentCreateRequest {
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}
