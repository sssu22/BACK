package com.example.trendlog.domain.post;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PostSearchCondition {
    private String keyword;
    private String emotion;
    private UUID userId;
}

