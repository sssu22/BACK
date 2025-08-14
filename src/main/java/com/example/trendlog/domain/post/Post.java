package com.example.trendlog.domain.post;

import com.example.trendlog.domain.user.User;
import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.dto.request.post.PostCreateUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;
    private String title;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trend_id")
    private Trend trend;
    private String description;
    private String location; // 위치명
    private Double latitude; // 위도
    private Double longitude; // 경도
    private String district; // 구 이름
    private LocalDate experienceDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Builder.Default
    private boolean deleted = false;
    @Enumerated(EnumType.STRING)
    private Emotion emotion;
    @Builder.Default
    private int viewCount = 0;
    @Builder.Default
    private int commentCount = 0;
    @Builder.Default
    private int likeCount = 0;
    @Builder.Default
    private int scrapCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    List<PostTag> tags = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }


    public void addViewCount() {
        this.viewCount++;
    }

    public void addCommentCount() {
        this.commentCount++;
    }

    public void changeLikeCount(int delta) {
        this.likeCount = Math.max(0, this.likeCount + delta);
    }

    public void changeScrapCount(int delta) {
        this.scrapCount = Math.max(0, this.scrapCount + delta);
    }

    public void changeIsDeleted() {
        this.deleted = true;
    }

    public void update(PostCreateUpdateRequest request, String district, Trend trend) {
        this.title = request.getTitle();
        this.trend = trend;
        this.experienceDate = request.getExperienceDate();
        this.location = request.getLocation();
        this.latitude = request.getLatitude();
        this.longitude = request.getLongitude();
        this.district = district;
        this.emotion = Emotion.from(request.getEmotion());
        this.description = request.getDescription();
        this.updatedAt = LocalDateTime.now();
    }

}
