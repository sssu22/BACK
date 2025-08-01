package com.example.trendlog.domain.trend;

import com.example.trendlog.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserTrendRecommendation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trend_id", nullable = false)
    private Trend trend;

    private int rank;

    private LocalDateTime createAt = LocalDateTime.now();


    public UserTrendRecommendation(User user, Trend trend, int rank) {
        this.user = user;
        this.trend = trend;
        this.rank = rank;
    }


}
