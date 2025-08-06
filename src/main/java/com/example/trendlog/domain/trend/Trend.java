package com.example.trendlog.domain.trend;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@AllArgsConstructor
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Trend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trend_id", updatable = false, nullable = false)
    private Long id;

    private  String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    private TrendCategory category;

    private Integer score;

    @Builder.Default
    private  Integer viewCount=0;

    @Builder.Default
    private Integer likeCount=0;

    @Builder.Default
    private Integer scrapCount=0;

    @Builder.Default
    private Integer commentCount = 0;

    @Builder.Default
    @Setter
    private Integer snsMentions=0; //유튜브 언급량(영상 개수)

    @Builder.Default
    @Setter
    private Long youtubeTopView=0L; //유튜브 top 영상들의 조회수

    @Builder.Default
    @Setter
    private String peakPeriod="피크타임이 없음";

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Setter
    private Integer previousScore;

    //아래 두개는 빈 리스트로 저장->추후 AI 적용 시 구현 예정
    @Builder.Default
    @ElementCollection
    private List<String> tags = new ArrayList<>();

    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "trend_similar_trends",
            joinColumns = @JoinColumn(name = "trend_id"),
            inverseJoinColumns = @JoinColumn(name = "similar_trend_id")
    )
    private List<Trend> similarTrends = new ArrayList<>();

    /*
    뉴스 추천 및 뉴스 점수
     */
    @OneToMany(mappedBy = "trend", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RecommendedNews> recommendedNewsList = new ArrayList<>();

    @Builder.Default
    @Setter
    private Integer newsScore = 60;

    public void addRecommendedNews(RecommendedNews news) {
        recommendedNewsList.add(news);
        news.setTrend(this);
    }

    public void clearRecommendedNews() {
        this.recommendedNewsList.clear();
    }

    public void updateNewsScore(Integer score) {
        this.newsScore = score;
    }


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }

    public void increaseScrapCount() {
        this.scrapCount++;
    }

    public void decreaseScrapCount() { this.scrapCount = Math.max(0, this.scrapCount - 1); }

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        this.commentCount = Math.max(0, this.commentCount - 1);
    }
}
