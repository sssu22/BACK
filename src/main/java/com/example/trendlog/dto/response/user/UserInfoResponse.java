package com.example.trendlog.dto.response.user;

import com.example.trendlog.domain.user.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class UserInfoResponse {
    private String email;
    private String name;
    private String profileImage;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private String address;
    private String stateMessage;
//    private Boolean publicProfile;
    private Boolean locationTracing;
//    private Boolean alarm;
    public static UserInfoResponse from(User user) {
        return UserInfoResponse.builder()
                .email(user.getEmail())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .birth(user.getBirth())
                .address(user.getAddress())
                .stateMessage(user.getStateMessage())
                .locationTracing(user.getLocationTracing())
                .build();
    }

}
