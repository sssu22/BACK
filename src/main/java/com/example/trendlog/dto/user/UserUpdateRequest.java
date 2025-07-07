package com.example.trendlog.dto.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UserUpdateRequest {
    @Size(max=50)
    private String name;
    @Size(max=200)
    private String stateMessage;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birth;
    private String address;
    private String profileImage;
//    private Boolean publicProfile;
    private Boolean locationTracing;
//    private Boolean alarm;
}
