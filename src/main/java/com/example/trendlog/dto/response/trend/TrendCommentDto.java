package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.TrendComment;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Builder
public class TrendCommentDto {
    private final Long commentId;
    private final String content;
    private final String authorName;
    private final String authorProfileImageUrl;
    private final String createAt;
    private final Integer likeCount;
    private final boolean isLiked;

    public static TrendCommentDto from(TrendComment comment,boolean isLiked) {
        return TrendCommentDto.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .authorName(comment.getUser().getName())
                .authorProfileImageUrl(comment.getUser().getProfileImage())
                .createAt(formatTime(comment.getCreatedAt()))
                .likeCount(comment.getLikeCount())
                .isLiked(isLiked)
                .build();
    }

    private static String formatTime(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        long minutes = duration.toMinutes();
        long hours = duration.toHours();

        if (minutes < 1) return "방금 전";
        else if (minutes < 60) return minutes + "분 전";
        else if (hours < 24) return hours + "시간 전";
        else return createdAt.toLocalDate().toString(); // 2025-07-21
    }
}
