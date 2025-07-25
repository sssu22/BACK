package com.example.trendlog.dto.response.post;

import com.example.trendlog.domain.post.DistrictLocationProvider;
import com.example.trendlog.domain.post.LatLng;
import com.example.trendlog.global.exception.AppException;
import lombok.Builder;
import lombok.Getter;

import static com.example.trendlog.global.exception.code.PostErrorCode.POST_MAP_LOCATION_NOT_FOUND;

@Getter
@Builder
public class PostMapResponse {
    private String district;
    private int postCount;
    private double latitude;     // 구의 중심 위도
    private double longitude;    // 구의 중심 경도

    public static PostMapResponse from(String district, int postCount) {
        LatLng latLng = DistrictLocationProvider.getLatLngByDistrict(district);

        if (latLng == null) {
            throw new AppException(POST_MAP_LOCATION_NOT_FOUND);
        }

        return PostMapResponse.builder()
                .district(district)
                .postCount(postCount)
                .latitude(latLng.getLatitude())
                .longitude(latLng.getLongitude())
                .build();
    }

}
