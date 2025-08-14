package com.example.trendlog.repository.post;

import com.example.trendlog.domain.post.Post;
import com.example.trendlog.domain.post.PostSearchCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> searchAll(PostSearchCondition condition, Pageable pageable);

    Page<Post> searchMy(PostSearchCondition condition, Pageable pageable);

    Page<Post> searchScrapped(PostSearchCondition condition, Pageable pageable);

}
