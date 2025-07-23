package com.example.trendlog.domain.post;

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
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;
    private String name;
    private LocalDateTime createdAt;

    public static Tag of(String name) {
        return Tag.builder()
                .name(name)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
