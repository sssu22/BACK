package com.example.trendlog.domain.trend;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class TrendSearchCondition {
    private String keyword;
    private String category;
    private UUID userId;
}
