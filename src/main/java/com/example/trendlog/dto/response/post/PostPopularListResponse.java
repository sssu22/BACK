package com.example.trendlog.dto.response.post;

import com.example.trendlog.domain.post.Post;
import com.example.trendlog.domain.post.PostStatistics;

public record PostPopularListResponse (
        Long id,
        String title,
        int trendScore,
        String tag
) {
    // from
    public static PostPopularListResponse from(PostStatistics postStat) {
        Post post = postStat.getPost();
        return new PostPopularListResponse(
                post.getId(),
                post.getTitle(),
                100, // post.trend.trendScore()
                post.getTags().isEmpty() ? null : post.getTags().get(0).getTag().getName()
        );
    }
}
