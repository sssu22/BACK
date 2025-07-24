package com.example.trendlog.global.docs;

import com.example.trendlog.dto.response.post.TagPopularPagedResponse;
import com.example.trendlog.global.dto.DataResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Tags", description = "태그 관련 API")
public interface TagSwaggerSpec {

    @Operation(summary = "인기 태그 목록 조회", description = "전체 태그 중 가장 많이 사용된 태그를 랭킹 순으로 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인기 태그 목록 조회 성공")
    })
    public ResponseEntity<DataResponse<TagPopularPagedResponse>> getPopularTags(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    );
}
