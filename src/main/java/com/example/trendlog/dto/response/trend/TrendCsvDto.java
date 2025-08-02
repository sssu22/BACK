package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.TrendCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TrendCsvDto {
    private Long trendId;
    private String title;
    private String category;
    private String description;
    public TrendCsvDto(Long trendId, String title, TrendCategory category, String description) {
        this.trendId = trendId;
        this.title = title;
        this.category = category.name(); // enum → String
        this.description = description;
    }
}
