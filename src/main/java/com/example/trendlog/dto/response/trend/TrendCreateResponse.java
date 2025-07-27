package com.example.trendlog.dto.response.trend;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrendCreateResponse {
    private Long id;
    private String name;
    private String category;
}