package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendCategory;
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
    private final TrendCategory category;
    private final Integer score;
    private final Integer viewCount;
    private final Integer likeCount;
    private final Integer commentCount;
    private final Integer snsMentions;
    private final String peakPeriod;
    private final List<String> tags;
    private final List<SimilarTrendDto> similarTrends;
    private final LocalDateTime createdAt;
    private final List<TrendCommentDto> comments;

    public TrendDetailResponse(Long id, String title, String description, TrendCategory category, Integer score, Integer viewCount, Integer likeCount, Integer commentCount, Integer snsMentions, String peakPeriod, List<String> tags, List<SimilarTrendDto> similarTrends, LocalDateTime createdAt,List<TrendCommentDto> comments) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.score = score;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.snsMentions = snsMentions;
        this.peakPeriod = peakPeriod;
        this.tags = tags;
        this.similarTrends = similarTrends;
        this.createdAt = createdAt;
        this.comments = comments;
    }

    public static TrendDetailResponse from(Trend trend, List<TrendCommentDto> comments) {
        return TrendDetailResponse.builder()
                .id(trend.getId())
                .title(trend.getTitle())
                .description(trend.getDescription())
                .category(trend.getCategory())
                .score(trend.getScore())
                .viewCount(trend.getViewCount())
                .likeCount(trend.getLikeCount())
                .commentCount(trend.getCommentCount())
                .snsMentions(trend.getSnsMentions() != null ? trend.getSnsMentions() : 0)
                .peakPeriod(trend.getPeakPeriod() != null ? trend.getPeakPeriod() : "미정")
                .tags(trend.getTags()!=null?trend.getTags() : List.of())
                .similarTrends(
                        trend.getSimilarTrends() != null
                                ? trend.getSimilarTrends().stream()
                                .map(SimilarTrendDto::from)
                                .collect(Collectors.toList())
                                : List.of()
                )
                .comments(comments)
                .createdAt(trend.getCreatedAt())
                .build();

    }
}
