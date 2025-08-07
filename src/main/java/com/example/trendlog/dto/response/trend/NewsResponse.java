package com.example.trendlog.dto.response.trend;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class NewsResponse {
    private final String title;
    private final String link;
}
