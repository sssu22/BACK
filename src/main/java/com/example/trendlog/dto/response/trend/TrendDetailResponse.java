package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Builder
public class TrendDetailResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final String category;
    private final Integer score;
    private final Integer viewCount;
    private final Integer likeCount;
    private final Integer commentCount;
    private final Integer snsMentions;
    private final Long youtubeTopView;
    @Schema(example = "2025년 7월")
    private final String peakPeriod;
    private final List<String> tags;
    private final List<SimilarTrendDto> similarTrends;
    private final LocalDateTime createdAt;
    private final boolean isLiked;
    private final boolean isScrapped;
    private final List<TrendCommentDto> comments;


    public static TrendDetailResponse from(Trend trend, List<TrendCommentDto> comments,boolean isLiked, boolean isScrapped) {
        return TrendDetailResponse.builder()
                .id(trend.getId())
                .title(trend.getTitle())
                .description(trend.getDescription())
                .category(trend.getCategory().getDescription())
                .score(trend.getScore())
                .viewCount(trend.getViewCount())
                .likeCount(trend.getLikeCount())
                .commentCount(trend.getCommentCount())
                .snsMentions(trend.getSnsMentions() != null ? trend.getSnsMentions() : 0)
                .youtubeTopView(trend.getYoutubeTopView())
                .peakPeriod(trend.getPeakPeriod() != null ? trend.getPeakPeriod() : "미정")
                .tags(trend.getTags()!=null?trend.getTags() : List.of())
                .similarTrends(
                        trend.getSimilarTrends() != null
                                ? trend.getSimilarTrends().stream()
                                .map(SimilarTrendDto::from)
                                .collect(Collectors.toList())
                                : List.of()
                )
                .isLiked(isLiked)
                .isScrapped(isScrapped)
                .comments(comments)
                .createdAt(trend.getCreatedAt())
                .build();

    }
}
