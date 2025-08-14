package com.example.trendlog.dto.response.post;

import com.example.trendlog.domain.post.Post;
import com.example.trendlog.domain.post.PostStatistics;
import com.example.trendlog.domain.post.Tag;
import com.example.trendlog.domain.post.TagStatistics;

public record TagPopularListResponse(
        Long id,
        String name,
        int rank,
        Long postCount // 경험 수
) {
    // from
    public static TagPopularListResponse from(TagStatistics tagStatistics, int rank) {
        Tag tag = tagStatistics.getTag();
        return new TagPopularListResponse(
                tag.getId(),
                tag.getName(),
                rank,
                tagStatistics.getTotalCount() == null ? 0 : tagStatistics.getTotalCount()
        );
    }
}
