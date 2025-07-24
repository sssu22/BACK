package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.TrendComment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TrendCommentDto {
    private final Long commentId;
    private final String content;
    private final String authorName;
    private final String authorProfileImageUrl;
    private final LocalDateTime createAt;
    private final Integer likeCount;

    public static TrendCommentDto from(TrendComment comment) {
        return TrendCommentDto.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getUser().getName())
                .authorProfileImageUrl(comment.getUser().getProfileImage())
                .createAt(comment.getCreatedAt())
                .likeCount(comment.getLikeCount())
                .build();
    }
}
