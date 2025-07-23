package com.example.trendlog.dto.request.post;

import com.example.trendlog.domain.User;
import com.example.trendlog.domain.post.Emotion;
import com.example.trendlog.domain.post.Post;
import com.example.trendlog.global.exception.AppException;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

import static com.example.trendlog.global.exception.code.PostErrorCode.POST_EMPTY_FIELD;

@Getter
@Builder
public class PostCreateUpdateRequest {
    private String title;
    private Long trendId;
    private LocalDate experienceDate;
    private String location;
    private String emotion;
    private List<String> tags; // nullable
    private String description; // nullable

    public Post toEntity(User user) {
        return Post.builder()
                .title(title)
                .trendId(trendId)
                .experienceDate(experienceDate)
                .location(location)
                .emotion(Emotion.from(emotion))
                .description(description)
                .user(user)
                .build();
    }


    public void validate() {
        if (title == null || title.isBlank() || trendId == null || experienceDate == null
                || location == null || location.isBlank() || emotion == null || emotion.isBlank() ) {
            throw new AppException(POST_EMPTY_FIELD);
        }
    }
}
