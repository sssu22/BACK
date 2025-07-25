package com.example.trendlog.controller;

import com.example.trendlog.dto.response.post.TagPopularPagedResponse;
import com.example.trendlog.global.docs.TagSwaggerSpec;
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
public class TagController implements TagSwaggerSpec {
    private final TagService tagService;

    @GetMapping("/popular")
    public ResponseEntity<DataResponse<TagPopularPagedResponse>> getPopularTags(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(DataResponse.from(tagService.getPopularTagList(page, size)));
    }
}
