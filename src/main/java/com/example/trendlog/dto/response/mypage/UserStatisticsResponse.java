package com.example.trendlog.dto.response.mypage;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserStatisticsResponse {
    private final int postCount; // 내가 작성한 게시글 수
    private final int averageScore; // 내가 작성한 게시글들의 트렌드 평균 점수
    private final int visitPlaceCount; // 구별 방문 지역 수
    private final int scrapCount;  // 게시글 + 트렌드 스크랩 수 합산

    public static UserStatisticsResponse from(int postCount, int averageScore, int visitPlaceCount, int scrapCount) {
        return UserStatisticsResponse.builder()
                .postCount(postCount)
                .averageScore(averageScore)
                .visitPlaceCount(visitPlaceCount)
                .scrapCount(scrapCount)
                .build();
    }
}
