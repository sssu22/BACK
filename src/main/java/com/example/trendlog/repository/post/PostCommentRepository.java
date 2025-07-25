package com.example.trendlog.repository.post;

import com.example.trendlog.domain.post.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    List<PostComment> findAllByPostIdAndDeletedFalseOrderByCreatedAt(Long postId);
}
