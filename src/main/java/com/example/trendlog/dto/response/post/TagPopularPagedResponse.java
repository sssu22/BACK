package com.example.trendlog.dto.response.post;

import com.example.trendlog.domain.post.PostStatistics;
import com.example.trendlog.domain.post.TagStatistics;
import org.springframework.data.domain.Page;

import java.util.List;

public record TagPopularPagedResponse(
        List<TagPopularListResponse> list,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages
) {
    public static TagPopularPagedResponse from(List<TagPopularListResponse> list, Page<TagStatistics> posts) {
        return new TagPopularPagedResponse(
                list,
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages()
        );
    }
}
