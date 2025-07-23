package com.example.trendlog.domain.post;

import com.example.trendlog.domain.User;
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
    private Long trendId; // 우선 trendId 받게끔 -> 추후 수정
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private String location;
    // 위도, 경도 추가
    private LocalDate experienceDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Column(name = "is_deleted")
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

    public void update(PostCreateUpdateRequest request) {
        this.title = request.getTitle();
        this.trendId = request.getTrendId();
        this.experienceDate = request.getExperienceDate();
        this.location = request.getLocation();
        this.emotion = Emotion.from(request.getEmotion());
        this.description = request.getDescription();
        this.updatedAt = LocalDateTime.now();
    }

}
