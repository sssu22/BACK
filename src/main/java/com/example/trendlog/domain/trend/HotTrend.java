package com.example.trendlog.domain.trend;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "hot_trend")
public class HotTrend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int trendScore;

    @Column(nullable = false)
    private LocalDateTime period;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trend_id", nullable = false)
    private Trend trend;

    @Column(nullable=false)
    private int increaseScore;

    public HotTrend(String name, int trendScore, LocalDateTime period, Trend trend, int increaseScore) {
        this.name = name;
        this.trendScore = trendScore;
        this.period = period;
        this.trend = trend;
        this.increaseScore = increaseScore;
    }

    public static HotTrend of(Trend trend, int increaseScore, LocalDateTime now) {
        return new HotTrend(
                trend.getTitle(),
                trend.getScore(),
                now,
                trend,
                increaseScore
        );
    }
}
