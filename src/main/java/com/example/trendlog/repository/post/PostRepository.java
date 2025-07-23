package com.example.trendlog.repository.post;

import com.example.trendlog.domain.post.Emotion;
import com.example.trendlog.domain.post.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 삭제되지 않은 게시글을 최신순으로 조회
    Page<Post> findAllByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    // 삭제가 되지 않은 게시글을 게시글 ID로 조회
    Optional<Post> findByIdAndDeletedFalse(Long postId);

    // 삭제가 되지 않은 게시글을 감정 타입으로 조회
    Page<Post> findAllByDeletedFalseAndEmotion(Emotion emotion, Pageable pageable);

    Page<Post> findAllByDeletedFalse(Pageable pageable);


}
