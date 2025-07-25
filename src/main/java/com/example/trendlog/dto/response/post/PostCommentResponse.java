package com.example.trendlog.dto.response.post;

import com.example.trendlog.domain.post.PostComment;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Builder
public class PostCommentResponse {
    private Long id;
    private String userName;
    private String time;
    private String content;
    private int likeCount;

    public static PostCommentResponse from(PostComment postComment) {
        return PostCommentResponse.builder()
                .id(postComment.getId())
                .userName(postComment.getUser().getName())
                .time(formatTime(postComment.getCreatedAt()))
                .content(postComment.isDeleted()?"[삭제된 댓글입니다.]":postComment.getContent())
                .likeCount(postComment.getLikeCount())
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
