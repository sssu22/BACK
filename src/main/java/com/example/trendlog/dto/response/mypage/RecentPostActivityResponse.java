package com.example.trendlog.dto.response.mypage;

import com.example.trendlog.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RecentPostActivityResponse {
    Long id;
    String title;
    LocalDate experienceDate;
    int trendScore;
    String emotion;

    public static RecentPostActivityResponse from(Post post) {
        return new RecentPostActivityResponse(
                post.getId(),
                post.getTitle(),
                post.getExperienceDate(),
                post.getTrend().getScore(),
                post.getEmotion().name()
        );
    }
}
