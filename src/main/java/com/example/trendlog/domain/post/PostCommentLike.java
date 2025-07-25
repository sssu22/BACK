package com.example.trendlog.domain.post;

import com.example.trendlog.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "post_comment_like", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "post_comment_id"})
})
public class PostCommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_comment_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_comment_id")
    private PostComment postComment;

    public static PostCommentLike of(User user, PostComment postComment) {
        return PostCommentLike.builder()
                .user(user)
                .postComment(postComment)
                .build();
    }
}
