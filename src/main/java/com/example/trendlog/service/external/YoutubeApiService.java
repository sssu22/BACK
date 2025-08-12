package com.example.trendlog.service.external;

import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.GoogleErrorCode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class YoutubeApiService {
    @Value("${youtube.api.key}")
    private String apiKey;

    @Value("${youtube.api.base-url}")
    private String youtubeBaseUrl;

    private final WebClient webClient;

    public YoutubeApiService() {
        this.webClient = WebClient.builder().build();
    }

    //트렌드 이름으로 유튜브 영상 검색
    public List<String> getYoutubeVideoIds(String query, String publishedAfter, String publishedBefore) {
        try{
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url=youtubeBaseUrl+"/search"
                    +"?part=snippet"
                    +"&type=video"
                    +"&q="+encodedQuery
                    +"&publishedAfter="+publishedAfter
                    +"&publishedBefore="+publishedBefore
                    +"&maxResults=50"
                    +"&key="+apiKey;
            String response= webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            JsonArray items = json.getAsJsonArray("items");

            List<String> videoIds = new ArrayList<>();
            for (JsonElement item : items) {
                JsonObject itemObj = item.getAsJsonObject();
                JsonObject idObject = itemObj.getAsJsonObject("id");
                String videoId = idObject.get("videoId").getAsString();
                videoIds.add(videoId);
            }
            log.info("유튜브 영상 검색 결과, query='{}', 기간='{}~{}' : {}개 -> {}",
                    query, publishedAfter, publishedBefore, videoIds.size(), videoIds);
            return videoIds;
        }catch (Exception e){
            throw new AppException(GoogleErrorCode.YOUTUBE_API_REQUEST_FAIL);
        }
    }

    //상위 N개 영상 누적 조회수
    public long getTotalViewsOfTopNVideos(List<String> videoIds, int topN) {
        if(videoIds.isEmpty()) return 0L;

        try{
            String joinedIds = String.join(",", videoIds);

            String url=youtubeBaseUrl+"/videos"
                    +"?part=statistics"
                    +"&id="+joinedIds
                    +"&key="+apiKey;

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonObject json = JsonParser.parseString(response).getAsJsonObject();
            JsonArray items = json.getAsJsonArray("items");

            List<Long> views = new ArrayList<>();
            for (JsonElement item : items) {
                JsonObject itemObj = item.getAsJsonObject();
                JsonObject stats = itemObj.getAsJsonObject("statistics");
                long viewCount = stats.get("viewCount").getAsLong();
                views.add(viewCount);
            }
            log.info("조회수 목록: {}", views);

            long totalViews = views.stream()
                    .sorted(Comparator.reverseOrder())
                    .limit(topN)
                    .mapToLong(Long::longValue)
                    .sum();

            log.info("상위 {}개 영상의 누적 조회수: {}", topN, totalViews);

            return totalViews;

        }catch (Exception e){
            throw new AppException(GoogleErrorCode.YOUTUBE_API_REQUEST_FAIL);
        }

    }
    public static String[] getLastMonthRange(){
        LocalDate now=LocalDate.now();
        LocalDate firstDayOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDate firstDayOfThisMonth = now.withDayOfMonth(1);

        String publishedAfter = firstDayOfLastMonth.atStartOfDay(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT);
        String publishedBefore = firstDayOfThisMonth.atStartOfDay(ZoneId.of("UTC")).format(DateTimeFormatter.ISO_INSTANT);
        return new String[]{publishedAfter, publishedBefore};

    }

}
