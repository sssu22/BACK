package com.example.trendlog.dto.response.post;

import com.example.trendlog.domain.post.Post;

import java.time.LocalDate;
import java.util.List;

public record PostListResponse(
        Long id,
        String title,
        LocalDate experienceDate,
        String location, // ex) 강남구 신사동
        String summary, // description one line 보여주기
        String emotion,
        String trendTitle,
        int trendScore,
        List<String> tags
) {
    public static PostListResponse from(Post post) {
        String description = post.getDescription();
        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getExperienceDate(),
                post.getLocation(),
                description != null ? (description.length()<30?description:description.substring(0,30)) : null,
                post.getEmotion().name(),
                post.getTrend().getTitle(), // 트렌드 이름
                post.getTrend().getScore(),
                post.getTags().stream()
                        .map(tag -> tag.getTag().getName())
                        .toList()
        );
    }
}
