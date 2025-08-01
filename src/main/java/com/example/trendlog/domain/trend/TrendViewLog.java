package com.example.trendlog.domain.trend;

import com.example.trendlog.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class TrendViewLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Trend trend;

    private LocalDateTime viewedAt = LocalDateTime.now();

    public TrendViewLog(User user, Trend trend) {
        this.user = user;
        this.trend = trend;
    }
}
