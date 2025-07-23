package com.example.trendlog.dto.request.trend;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class TrendCreateRequest {
    @NotBlank(message = "트렌드 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "트렌드 설명은 필수입니다.")
    private String description;

    @Schema(description = "트렌드 카테고리 (예: FOOD, LIFESTYLE, CULTURE, HEALTH, INVESTMENT, SOCIAL, ETC)", example = "FOOD")
    @NotBlank(message = "트렌드 카테고리는 필수입니다.")
    private String category;
}
