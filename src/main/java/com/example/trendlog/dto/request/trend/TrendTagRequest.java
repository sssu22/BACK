package com.example.trendlog.dto.request.trend;

import lombok.Getter;

@Getter
public class TrendTagRequest {
    private String title;
    private String description;
    public TrendTagRequest(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
