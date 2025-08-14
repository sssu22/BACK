package com.example.trendlog.dto.request.trend;

import lombok.Getter;

@Getter
public class SimilarTrendRequest {
    private String title;
    private String description;
    private String category;

    public SimilarTrendRequest(String title, String description, String category) {
        this.title = title;
        this.description = description;
        this.category = category;
    }
}
