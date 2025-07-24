package com.example.trendlog.domain.trend;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    private  Integer viewCount=0;

    private Integer likeCount=0;

    private Integer commentCount = 0;

    private Integer snsMentions;

    private String peakPeriod;

    private LocalDateTime createdAt;

    @Setter
    private Integer previousScore;

    //아래 두개는 빈 리스트로 저장->추후 AI 적용 시 구현 예정
    @ElementCollection
    private List<String> tags = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "trend_similar_trends",
            joinColumns = @JoinColumn(name = "trend_id"),
            inverseJoinColumns = @JoinColumn(name = "similar_trend_id")
    )
    private List<Trend> similarTrends = new ArrayList<>();


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @Builder
    public Trend (String title, String description, String category, Integer score, List<String> tags, List<Trend> similarTrends){
        this.title = title;
        this.description = description;
        this.category = TrendCategory.valueOf(category);
        this.score = score;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.similarTrends = similarTrends != null ? similarTrends : new ArrayList<>();
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

    public void increaseCommentCount() {
        this.commentCount++;
    }

    public void decreaseCommentCount() {
        this.commentCount = Math.max(0, this.commentCount - 1);
    }
}
