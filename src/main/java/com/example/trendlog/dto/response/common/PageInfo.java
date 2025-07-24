package com.example.trendlog.dto.response.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
public class PageInfo {
    private final int page;
    private final int size;
    private final int totalElements;
    private final int totalPages;

    @Builder
    public PageInfo(int page, int size, int totalElements, int totalPages) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public static PageInfo from(Page<?> page){
        return PageInfo.builder()
                .page(page.getNumber() + 1)
                .size(page.getSize())
                .totalElements((int) page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }
}
