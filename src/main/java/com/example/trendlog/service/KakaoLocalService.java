package com.example.trendlog.service;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.trend.TrendNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.example.trendlog.global.exception.code.CommonErrorCode.INTERNAL_SERVER_ERROR;

@Service
public class KakaoLocalService {

    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String getDistrictByCoordinates(Double latitude, Double longitude) {
        String url = String.format(
                "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=%f&y=%f",
                longitude, latitude
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoRestApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            // documents 배열 첫 번째 항목(region_2depth_name)이 구 이름
            JsonNode documents = root.path("documents");
            if (documents.isArray() && documents.size() > 0) {
                String district = documents.get(0).path("region_2depth_name").asText();
                return district;
            }
        } catch (Exception e) {
            throw new AppException(INTERNAL_SERVER_ERROR);

        }
        return null;  // 실패 시 null 반환
    }
}

