package com.example.trendlog.domain.trend;

import com.example.trendlog.global.exception.AppException;
import lombok.Getter;

import static com.example.trendlog.global.exception.code.TrendErrorCode.INVALID_CATEGORY;

//public enum TrendCategory {
//    FOOD,         // 음식
//    LIFESTYLE,    // 라이프스타일
//    CULTURE,      // 문화
//    HEALTH,       // 건강
//    INVESTMENT,   // 투자
//    SOCIAL,       // 소셜
//    ETC           // 기타
//    //더 추가하는게 좋을까요?
//}

@Getter
public enum TrendCategory {
    FOOD("음식"),
    LIFESTYLE("라이프스타일"),
    CULTURE("문화"),
    HEALTH("건강"),
    INVESTMENT("투자"),
    SOCIAL("사회"),
    ETC("기타");

    private final String description;

    TrendCategory(String description) {
        this.description = description;
    }

    public static TrendCategory from(String keyword) {
        for (TrendCategory category : TrendCategory.values()) {
            if (category.name().equalsIgnoreCase(keyword) || category.getDescription().equalsIgnoreCase(keyword)) {
                return category;
            }
        }
        throw new AppException(INVALID_CATEGORY);
    }
}

