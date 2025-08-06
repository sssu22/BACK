package com.example.trendlog.dto.response.trend;

import lombok.Getter;

import java.util.List;
@Getter
public class SimilarTrendIdsResponse {
    private List<Long> similar_trend_ids;
}
