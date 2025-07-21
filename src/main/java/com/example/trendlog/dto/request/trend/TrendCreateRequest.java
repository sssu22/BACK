package com.example.trendlog.dto.request.trend;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class TrendCreateRequest {
    private String title;
    private String description;
    private String category;
}
