package com.example.trendlog.dto.response.mypage;

import com.example.trendlog.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProfileResponse {
    private final String name;
    private final String profileImage;
    private final LocalDateTime signDate;
    private String stateMessage;

    public static ProfileResponse from(User user) {
        return ProfileResponse.builder()
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .signDate(user.getSignDate())
                .stateMessage(user.getStateMessage())
                .build();
    }
}
