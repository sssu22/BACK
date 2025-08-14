package com.example.trendlog.dto.response.trend;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewsItemDto {
    private String keyword;
    private String title;
    private String link;
    private Integer score;
}
