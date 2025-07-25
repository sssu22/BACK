package com.example.trendlog.domain.post;

import java.util.Map;

public class DistrictLocationProvider {

    public static final Map<String, LatLng> DISTRICT_COORDS = Map.ofEntries(
            Map.entry("강남구", new LatLng(37.517236, 127.047325)),
            Map.entry("강동구", new LatLng(37.530126, 127.123770)),
            Map.entry("강북구", new LatLng(37.639749, 127.025620)),
            Map.entry("강서구", new LatLng(37.550937, 126.849538)),
            Map.entry("관악구", new LatLng(37.478406, 126.951613)),
            Map.entry("광진구", new LatLng(37.538484, 127.082293)),
            Map.entry("구로구", new LatLng(37.495485, 126.887506)),
            Map.entry("금천구", new LatLng(37.456872, 126.895188)),
            Map.entry("노원구", new LatLng(37.654358, 127.056142)),
            Map.entry("도봉구", new LatLng(37.668768, 127.047163)),
            Map.entry("동대문구", new LatLng(37.574368, 127.039558)),
            Map.entry("동작구", new LatLng(37.512409, 126.939252)),
            Map.entry("마포구", new LatLng(37.566324, 126.901636)),
            Map.entry("서대문구", new LatLng(37.582604, 126.935157)),
            Map.entry("서초구", new LatLng(37.483577, 127.032661)),
            Map.entry("성동구", new LatLng(37.563341, 127.036122)),
            Map.entry("성북구", new LatLng(37.589400, 127.016637)),
            Map.entry("송파구", new LatLng(37.514543, 127.105922)),
            Map.entry("양천구", new LatLng(37.517829, 126.866304)),
            Map.entry("영등포구", new LatLng(37.526371, 126.896228)),
            Map.entry("용산구", new LatLng(37.532602, 126.990377)),
            Map.entry("은평구", new LatLng(37.617612, 126.922700)),
            Map.entry("종로구", new LatLng(37.573050, 126.979189)),
            Map.entry("중구", new LatLng(37.563843, 126.997602)),
            Map.entry("중랑구", new LatLng(37.606991, 127.092831))
    );

    public static LatLng getLatLngByDistrict(String districtName) {
        return DISTRICT_COORDS.get(districtName);
    }
}
