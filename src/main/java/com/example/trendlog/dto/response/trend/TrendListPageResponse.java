package com.example.trendlog.dto.response.trend;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.dto.response.common.PageInfo;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class TrendListPageResponse {
    private final PageInfo pageInfo;
    private final List<TrendListResponse> content;

    @Builder
    public TrendListPageResponse(PageInfo pageInfo, List<TrendListResponse> content) {
        this.pageInfo = pageInfo;
        this.content = content;
    }

    public static TrendListPageResponse from(Page<Trend> page){
        return TrendListPageResponse.builder()
                .pageInfo(PageInfo.from(page))
                .content(page.getContent().stream()
                        .map(TrendListResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }


}
