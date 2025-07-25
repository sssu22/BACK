package com.example.trendlog.domain.post;

import com.example.trendlog.global.exception.AppException;
import lombok.Getter;

import static com.example.trendlog.global.exception.code.PostErrorCode.EMOTION_NOT_FOUND;

@Getter
public enum Emotion {
    JOY("기쁨"),
    EXCITEMENT("흥분"),
    NOSTALGIA("향수"),
    SURPRISE("놀라움"),
    LOVE("사랑");

    private final String description;

    Emotion(String description) {
        this.description = description;
    }

    public static Emotion from(String emotion) {
        for (Emotion e : Emotion.values()) {
            if (e.name().equalsIgnoreCase(emotion)) {
                return e;
            }
        }
        throw new AppException(EMOTION_NOT_FOUND);
    }
}
