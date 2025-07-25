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
    private int viewCount;
    private int likeCount;
    private int commentCount;
    private int scrapCount;
    private boolean isScrapped;
    private boolean isLiked;
    private boolean isEdited;
    private List<PostCommentResponse> comments;
    private String trendTitle;
    private int trendScore;
    private Long trendId;


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
                .trendTitle(post.getTrend().getTitle())
                .trendScore(post.getTrend().getScore())
                .trendId(post.getTrend().getId())
                .build();
    }
}
