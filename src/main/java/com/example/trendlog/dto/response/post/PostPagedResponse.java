package com.example.trendlog.dto.response.post;

import com.example.trendlog.domain.post.Post;
import com.example.trendlog.domain.post.PostStatistics;
import org.springframework.data.domain.Page;

import java.util.List;

public record PostPagedResponse (
        List<PostListResponse> list,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages
) {
    public static PostPagedResponse from(List<PostListResponse> list, Page<Post> posts) {
        return new PostPagedResponse(
                list,
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages()
        );
    }
}
