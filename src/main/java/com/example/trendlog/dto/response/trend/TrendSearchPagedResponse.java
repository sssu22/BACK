package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;
import org.springframework.data.domain.Page;

import java.util.List;

public record TrendSearchPagedResponse(
        List<TrendSearchListResponse> list,
        int currentPage,
        int pageSize,
        long totalElements,
        int totalPages
) {
    public static TrendSearchPagedResponse from(List<TrendSearchListResponse> list, Page<Trend> trends) {
        return new TrendSearchPagedResponse(
                list,
                trends.getNumber(),
                trends.getSize(),
                trends.getTotalElements(),
                trends.getTotalPages()
        );
    }
}
