package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.User;
import com.example.trendlog.domain.trend.TrendComment;
import com.example.trendlog.domain.trend.TrendCommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TrendCommentLikeReposity extends JpaRepository<TrendCommentLike,Long> {
    boolean existsByUserAndComment(User user,TrendComment comment);

    Optional<TrendCommentLike> findByUserAndComment(User user, TrendComment comment);

    @Query("SELECT cl.user.id, cl.comment.trend.id, 1 FROM TrendCommentLike cl GROUP BY cl.user.id, cl.comment.trend.id")
    List<Object[]> fetchTrendCommentLikeScores();

}
