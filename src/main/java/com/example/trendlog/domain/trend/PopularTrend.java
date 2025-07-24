package com.example.trendlog.domain.trend;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "popular_trend")
public class PopularTrend {
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


    public PopularTrend(String name, int trendScore, LocalDateTime period, Trend trend) {
        this.name = name;
        this.trendScore = trendScore;
        this.period = period;
        this.trend = trend;
    }

    public static PopularTrend of(Trend trend, LocalDateTime period) {
        return new PopularTrend(
                trend.getTitle(),
                trend.getScore(),
                period,
                trend
        );
    }
}
