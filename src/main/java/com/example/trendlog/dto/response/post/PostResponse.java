package com.example.trendlog.dto.response.post;

import com.example.trendlog.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String emotion;
    private List<String> tags;
    private LocalDate experienceDate;
    int viewCount;
    int likeCount;
    int commentCount;
    int scrapCount;
    boolean isScrapped;
    boolean isLiked;
    boolean isEdited;
    private List<PostCommentResponse> comments;


    public static PostResponse from(Post post, boolean isScrapped, boolean isLiked, List<PostCommentResponse> comments) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .description(post.getDescription())
                .location(post.getLocation())
                .emotion(post.getEmotion().name())
                .tags(post.getTags().stream()
                        .map(tag -> tag.getTag().getName())
                        .toList())
                .experienceDate(post.getExperienceDate())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .scrapCount(post.getScrapCount())
                .isScrapped(isScrapped)
                .isLiked(isLiked)
                .isEdited(!post.getUpdatedAt().equals(post.getCreatedAt()))
                .comments(comments)
                .build();
    }
}
