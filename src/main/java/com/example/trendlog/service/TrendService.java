package com.example.trendlog.service;

import com.example.trendlog.domain.trend.*;
import com.example.trendlog.domain.User;
import com.example.trendlog.dto.request.trend.TrendCreateRequest;
import com.example.trendlog.dto.response.trend.HotTrendResponse;
import com.example.trendlog.dto.response.trend.TrendDetailResponse;
import com.example.trendlog.dto.response.trend.TrendListPageResponse;
import com.example.trendlog.dto.response.trend.TrendListResponse;
import com.example.trendlog.global.exception.trend.TrendNotFoundException;
import com.example.trendlog.global.exception.user.UserNotFoundException;
import com.example.trendlog.repository.trend.*;
import com.example.trendlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrendService {

    private final TrendRepository trendRepository;
    private final UserRepository userRepository;
    private final TrendLikeRepository trendLikeRepository;
    private final TrendScrapRepository trendScrapRepository;
    private final PopularTrendRepository popularTrendRepository;
    private final HotTrendRepository hotTrendRepository;

    /**
     * 트렌드 생성
     */
    @Transactional
    public Long createTrend(TrendCreateRequest request) {
        Trend trend = Trend.builder().
                title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .score(ThreadLocalRandom.current().nextInt(60,101)) //임시 랜덤 점수
                .tags(new ArrayList<>()) // 초기 비어 있음
                .similarTrends(new ArrayList<>()) // 초기 비어 있음
                .build();
        Trend savedTrend = trendRepository.save(trend);
        return savedTrend.getId();
    }

    /**
     * 트렌드 상세 조회
     */
    public TrendDetailResponse getTrendById(Long id) {
        Trend trend = trendRepository.findById(id)
                .orElseThrow(TrendNotFoundException::new);//TREND-002
        return TrendDetailResponse.from(trend);
    }

    /**
     * 트렌드 목록
     */
    public TrendListPageResponse getTrendList(Pageable pageable) {
        Page<Trend> page=trendRepository.findAll(pageable);
        return TrendListPageResponse.from(page);
    }

    /**
     * 좋아요
     */
    @Transactional
    public void likeTrend(Long trendId, UUID userId){
        Trend trend = trendRepository.findById(trendId)
                .orElseThrow(TrendNotFoundException::new); //TREND-002

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new); //USER-001

        Optional<TrendLike> existingLike=trendLikeRepository.findByUserAndTrend(user,trend);
        if(existingLike.isPresent()){
            trendLikeRepository.delete(existingLike.get());
            trend.decreaseLikeCount();
        }else{
            TrendLike like=TrendLike.of(user,trend);
            trendLikeRepository.save(like);
            trend.increaseLikeCount();
        }
    }

    /**
     * 스크랩
     */
    @Transactional
    public void scrapTrend(Long trendId, UUID userId) {
        Trend trend = trendRepository.findById(trendId)
                .orElseThrow(TrendNotFoundException::new); //TREND-002

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new); //USER-001
        Optional<TrendScrap> existingScrap=trendScrapRepository.findByUserAndTrend(user,trend);
        if(existingScrap.isPresent()){
            trendScrapRepository.delete(existingScrap.get());
        }else{
            TrendScrap scrap=TrendScrap.of(user,trend);
            trendScrapRepository.save(scrap);
        }
    }

    /**
     * 최고 트렌드 저장(자정 기준)
     */
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    @Transactional
    public void updatePopularTrends(){
        //최근 1주일 이내 생성된 트렌드 기준
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusDays(7);

        List<Trend> topTrends=trendRepository.findTop5ByCreatedAtAfterOrderByScoreDesc(oneWeekAgo);

        List<PopularTrend> popularTrends = topTrends.stream()
                .map(trend->PopularTrend.of(trend,now))
                .collect(Collectors.toList());

        popularTrendRepository.saveAll(popularTrends);
    }

    /**
     * 최고 트렌드 목록 반환
     */
    public List<TrendListResponse> getLatestPopularTrends(){
        LocalDateTime latestPeriod=popularTrendRepository.findLatestPeriod();
        if(latestPeriod==null){
            return Collections.emptyList(); // 아직 저장된 게 없는 경우
        }
        List<PopularTrend> popularTrends = popularTrendRepository.findTop5ByPeriodOrderByTrendScoreDesc(latestPeriod);
        return popularTrends.stream()
                .map(popularTrend -> TrendListResponse.from(popularTrend.getTrend()))
                .toList();
    }

    /**
     * 최근 트렌드 저장(6시간 기준)
     */
    @Scheduled(cron = "0 0 0,6,12,18 * * ?") // 매일 0시, 6시, 12시, 18시에 실행
    @Transactional
    public void updateRecentTrends(){
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime sixHoursAge=now.minusHours(6);

        List<Trend> trends = trendRepository.findTop3ByScoreIncreaseSince(sixHoursAge);

        List<HotTrend> hotTrends = trends.stream()
                .map(trend->{
                    int increaseScore=trend.getScore()-trend.getPreviousScore();
                    trend.setPreviousScore(trend.getScore());
                    return HotTrend.of(trend,increaseScore,now);
                })
                .collect(Collectors.toList());

        hotTrendRepository.saveAll(hotTrends);
    }

    /**
     * 최근 트렌드 조회
     */
    public List<HotTrendResponse> getHotTrends(){
        LocalDateTime latestPeriod = hotTrendRepository.findLatestPeriod();
        if(latestPeriod==null){
            return Collections.emptyList();
        }
        List<HotTrend> hotTrends=hotTrendRepository.findTop3ByPeriodOrderByIncreaseScoreDesc(latestPeriod);
        return hotTrends.stream()
                .map(HotTrendResponse::from)
                .toList();
    }



}
