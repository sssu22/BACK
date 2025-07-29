package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.dto.response.common.PageInfo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;
@Builder
@Getter
public class TrendListPageResponse {
    private final List<TrendListResponse> content;
    private final int currentPage;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;

    public static TrendListPageResponse from(Page<Trend> page) {
        List<TrendListResponse> contentList = page.getContent()
                .stream()
                .map(TrendListResponse::from)
                .collect(Collectors.toList());

        return TrendListPageResponse.builder()
                .content(contentList)
                .currentPage(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
