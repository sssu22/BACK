package com.example.trendlog.domain.post;

import com.example.trendlog.domain.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "post_comments")
public class PostComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_comment_id")
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder.Default
    private boolean deleted = false;
    private LocalDateTime createdAt;

    @Builder.Default
    private int likeCount = 0;

    public void deleteComment() {
        this.deleted = true;
    }

    public void changeLikeCount(int delta) {
        this.likeCount = Math.max(0, this.likeCount + delta);
    }


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public static PostComment of(String content, User user, Post post) {
        return PostComment.builder()
                .content(content)
                .user(user)
                .post(post)
                .build();
    }
}
