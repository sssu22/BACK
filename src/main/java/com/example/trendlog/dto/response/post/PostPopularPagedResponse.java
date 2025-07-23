package com.example.trendlog.dto.response.post;

import com.example.trendlog.domain.post.PostStatistics;
import org.springframework.data.domain.Page;

import java.util.List;

public record PostPopularPagedResponse(
        List<PostPopularListResponse> list,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages
) {
    public static PostPopularPagedResponse from(List<PostPopularListResponse> list, Page<PostStatistics> posts) {
        return new PostPopularPagedResponse(
                list,
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages()
        );
    }
}
