package com.example.trendlog.domain.trend;

import com.example.trendlog.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrendCommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private TrendComment comment;

    public TrendCommentLike(User user, TrendComment comment) {
        this.user = user;
        this.comment = comment;
    }

    public static TrendCommentLike of(User user, TrendComment comment) {
        return new TrendCommentLike(user, comment);
    }
}
