package com.example.trendlog.service;

import com.example.trendlog.dto.response.trend.TrendRecommendScoreDto;
import com.example.trendlog.repository.post.PostLikeRepository;
import com.example.trendlog.repository.post.PostRepository;
import com.example.trendlog.repository.trend.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrendRecommendScoreExportService {
    private final TrendLikeRepository trendLikeRepository;
    private final TrendScrapRepository trendScrapRepository;
    private final TrendCommentRepository trendCommentRepository;
    private final TrendCommentLikeReposity trendCommentLikeReposity;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final TrendViewLogRepository trendViewLogRepository;

    //추천 학습을 위한 점수 계산
    public List<TrendRecommendScoreDto> getAllTrendScores(){
        List<TrendRecommendScoreDto> result = new ArrayList<>();
        result.addAll(mapToDto(trendLikeRepository.fetchTrendLikeScores()));
        result.addAll(mapToDto(trendScrapRepository.fetchTrendScrapScores()));
        result.addAll(mapToDto(trendCommentRepository.fetchTrendCommentScores()));
        result.addAll(mapToDto(trendCommentLikeReposity.fetchTrendCommentLikeScores()));
        result.addAll(mapToDto(postRepository.fetchPostWriteScores()));
        result.addAll(mapToDto(postLikeRepository.fetchPostLikeScores()));
        result.addAll(mapToDto(trendViewLogRepository.fetchTrendViewScores(LocalDateTime.now().minusDays(7))));

    // 통합 점수 합산
        Map<Pair<UUID, Long>, Integer> scoreMap = new HashMap<>();
        for (TrendRecommendScoreDto dto : result) {
            var key = Pair.of(dto.getUserId(), dto.getTrendId());
            scoreMap.put(key, scoreMap.getOrDefault(key, 0) + dto.getScore());
        }
        log.info("통합 점수 계산 완료.");
        return scoreMap.entrySet().stream()
                .map(e -> new TrendRecommendScoreDto(e.getKey().getLeft(), e.getKey().getRight(), e.getValue()))
                .toList();
    }

    private List<TrendRecommendScoreDto> mapToDto(List<Object[]> rawList) {
        List<TrendRecommendScoreDto> dtoList = new ArrayList<>();
        for (Object[] row : rawList) {
            UUID userId = (UUID) row[0];
            Long trendId = (Long) row[1];
            int score = ((Number) row[2]).intValue();
            dtoList.add(new TrendRecommendScoreDto(userId, trendId, score));
        }
        return dtoList;
    }

    // csv에 저장
    public void exportScoresToCsv(List<TrendRecommendScoreDto> scores) {
        File file = new File("trend_recommend_scores.csv");
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("user_id,trend_id,score"); // 헤더
            for (TrendRecommendScoreDto dto : scores) {
                writer.printf("%s,%d,%d%n", dto.getUserId(), dto.getTrendId(), dto.getScore());
            }
            log.info("CSV 파일 저장 완료: {}", file.getAbsolutePath());
        }catch (Exception e){
            log.error("CSV 저장 중 오류 발생", e);
        }
    }
}
