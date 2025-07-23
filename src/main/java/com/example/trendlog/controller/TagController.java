package com.example.trendlog.controller;

import com.example.trendlog.dto.response.post.TagPopularPagedResponse;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/tags")
@RequiredArgsConstructor
public class TagController {
    private final TagService tagService;

    @Operation(summary = "인기 태그 목록 조회", description = "전체 태그 중 가장 많이 사용된 태그를 랭킹 순으로 조회합니다.")
    @GetMapping("/popular")
    public ResponseEntity<DataResponse<TagPopularPagedResponse>> getPopularTags(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(DataResponse.from(tagService.getPopularTagList(page, size)));
    }
}
