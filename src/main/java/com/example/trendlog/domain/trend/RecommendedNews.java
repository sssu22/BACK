package com.example.trendlog.domain.trend;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RecommendedNews {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommended_news_id")
    private Long id;

    private String keyword;

    private String title;

    private String link;

    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trend_id")
    @Setter
    private Trend trend;
}
