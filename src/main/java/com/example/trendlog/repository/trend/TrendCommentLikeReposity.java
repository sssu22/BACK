package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.User;
import com.example.trendlog.domain.trend.TrendComment;
import com.example.trendlog.domain.trend.TrendCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrendCommentLikeReposity extends JpaRepository<TrendCommentLike,Long> {
    boolean existsByUserAndComment(User user,TrendComment comment);

    Optional<TrendCommentLike> findByUserAndComment(User user, TrendComment comment);
}
