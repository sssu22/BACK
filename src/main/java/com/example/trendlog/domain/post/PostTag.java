package com.example.trendlog.domain.post;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "post_tag")
public class PostTag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_tag_id")
    private Long id;

    @Setter
    private int sortOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public static PostTag of(Post post, Tag tag, int sortOrder) {
        return PostTag.builder()
                .post(post)
                .tag(tag)
                .sortOrder(sortOrder)
                .build();
    }

}
