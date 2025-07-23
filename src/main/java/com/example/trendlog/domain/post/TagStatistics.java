package com.example.trendlog.domain.post;

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
@Table(
        name = "tag_statistics",
        indexes = {
                @Index(name = "idx_tag_statistics_post_id", columnList = "tag_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_tag_statistics_tag_id", columnNames = "tag_id")
        }
)
public class TagStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_statistics_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    private Long totalCount;
}
