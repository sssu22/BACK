package com.example.trendlog.dto.request.auth;

import com.example.trendlog.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserSignupRequest {
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-]).{8,20}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 모두 포함한 8~20자여야 합니다."
    )
    private String password;

    public User toEntity(String encodedPassword) {
        return User.builder()
                .email(this.email)
                .password(encodedPassword)
                .name(this.name)
                .provider("local")
                .signDate(LocalDateTime.now())
                .publicProfile(true)
                .locationTracing(false)
                .alarm(true)
                .build();
    }
}
