package com.example.trendlog.dto.request.trend;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class RecommendSaveRequest {
    private UUID userId;
    private List<Long> trendIds;
}
