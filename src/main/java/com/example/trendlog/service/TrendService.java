package com.example.trendlog.service;

import com.example.trendlog.domain.trend.*;
import com.example.trendlog.domain.User;
import com.example.trendlog.dto.request.trend.TrendCreateRequest;
import com.example.trendlog.dto.response.trend.*;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.code.TrendErrorCode;
import com.example.trendlog.global.exception.trend.CommentNotFoundException;
import com.example.trendlog.global.exception.trend.DuplicateTrendException;
import com.example.trendlog.global.exception.trend.InvalidCategoryException;
import com.example.trendlog.global.exception.trend.TrendNotFoundException;
import com.example.trendlog.global.exception.user.UserAccessDeniedException;
import com.example.trendlog.global.exception.user.UserNotFoundException;
import com.example.trendlog.repository.post.PostRepository;
import com.example.trendlog.repository.trend.*;
import com.example.trendlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.example.trendlog.global.exception.code.UserErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TrendService {

    private final TrendRepository trendRepository;
    private final UserRepository userRepository;
    private final TrendLikeRepository trendLikeRepository;
    private final TrendScrapRepository trendScrapRepository;
    private final PopularTrendRepository popularTrendRepository;
    private final RecentTrendRepository recentTrendRepository;
    private final TrendCommentRepository trendCommentRepository;
    private final TrendCommentLikeReposity trendCommentLikeReposity;
    private final PostRepository postRepository;
    private final TrendViewLogService trendViewLogService;
    private final NewsRecommendationService newsRecommendationService;

    /**
     * 트렌드 생성
     */
    @Transactional
    public TrendCreateResponse createTrend(UUID userId,TrendCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new); // USER-001

        TrendCategory category;
        try {
            category = TrendCategory.valueOf(request.getCategory());
        } catch (IllegalArgumentException e) {
            throw new InvalidCategoryException(); // TREND-010
        }

        if (trendRepository.existsByTitleAndCategory(request.getTitle(),category)) {
            throw new DuplicateTrendException(); // TREND-001
        }

        Trend trend = Trend.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(TrendCategory.valueOf(request.getCategory()))  // 문자열 → enum 변환
                .score(ThreadLocalRandom.current().nextInt(60, 101))     // 랜덤 점수
                .build();

        Trend savedTrend = trendRepository.save(trend);

        // 추천 뉴스 생성 및 AI 뉴스 스코어
        List<RecommendedNews> newsList = newsRecommendationService.generateNewsForKeyword(request.getTitle());

        // 연관관계 설정 + 트렌드에 점수 추가
        if (!newsList.isEmpty()) {
            for (RecommendedNews news : newsList) {
                trend.addRecommendedNews(news);
            }
            trend.setNewsScore(newsList.get(0).getScore());
        }

        return new TrendCreateResponse(
                savedTrend.getId(),
                savedTrend.getTitle(),
                savedTrend.getCategory().name()
        );
    }

    /**
     * 트렌드 상세 조회
     */
    @Transactional
    public TrendDetailResponse getTrendDetail(User user, Long id) {
        Trend trend = trendRepository.findById(id)
                .orElseThrow(TrendNotFoundException::new);//TREND-002

        trend.increaseViewCount();

        List<TrendCommentDto> comments=trendCommentRepository.findByTrendOrderByCreatedAtDesc(trend).stream()
                .map(c->TrendCommentDto.from(c,(user!=null)&&trendCommentLikeReposity.existsByUserAndComment(user,c)))
                .collect(Collectors.toList());

        boolean isLiked = false;
        boolean isScrapped = false;

        if (user != null) {
            isLiked = trendLikeRepository.existsByTrendAndUser(trend, user);
            isScrapped = trendScrapRepository.existsByTrendAndUser(trend, user);
            trendViewLogService.logTrendView(user, id);

        }

        return TrendDetailResponse.from(trend,comments,isLiked,isScrapped);
    }

    /**
     * 트렌드 목록
     */
    public TrendListPageResponse getTrendList(String sort, String category, int page, int size){
        if(page<0||size<=0){
            throw new AppException(TrendErrorCode.INVALID_PAGE_REQUEST);
        }
        Sort sortObj = Sort.by(Sort.Direction.DESC, "score");
        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<Trend> trendPage;


        if (category.equalsIgnoreCase("all")) {
            trendPage = trendRepository.findAll(pageable);
        } else {
            TrendCategory categoryEnum;
            try {
                categoryEnum = TrendCategory.valueOf(category.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new AppException(TrendErrorCode.INVALID_CATEGORY);
            }
            trendPage = trendRepository.findByCategory(categoryEnum, pageable);
        }


        return TrendListPageResponse.from(trendPage);
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
            trend.decreaseScrapCount();
        }else{
            TrendScrap scrap=TrendScrap.of(user,trend);
            trendScrapRepository.save(scrap);
            trend.increaseScrapCount();
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
    public List<PopularTrendResponse> getPopularTrends(){
        LocalDateTime latestPeriod=popularTrendRepository.findLatestPeriod();
        if(latestPeriod==null){
            return Collections.emptyList(); // 아직 저장된 게 없는 경우
        }
        List<PopularTrend> popularTrends = popularTrendRepository.findTop5ByPeriodOrderByTrendScoreDesc(latestPeriod);
        return popularTrends.stream()
                .map(PopularTrendResponse::from)
                .toList();
    }

    /**
     * 최근 트렌드 저장(6시간 기준)
     */
    @Scheduled(cron = "0 0 0,6,12,18 * * ?") // 매일 0시, 6시, 12시, 18시에 실행
    @Transactional
    public void updateRecentTrends(){
        LocalDateTime now = LocalDateTime.now();

        List<Trend> allTrends=trendRepository.findAll();

        List<RecentTrend> recentTrends = allTrends.stream()
                .filter(trend -> trend.getPreviousScore() != null)
                .map(trend -> {
                    int increase = trend.getScore() - trend.getPreviousScore();
                    return RecentTrend.of(trend, increase, now);
                })
                .sorted(Comparator.comparingInt(RecentTrend::getIncreaseScore).reversed()
                        .thenComparing((RecentTrend rt) -> rt.getTrend().getScore(), Comparator.reverseOrder()))
                .limit(3)
                .toList();

        recentTrendRepository.saveAll(recentTrends);

        allTrends.forEach(trend -> trend.setPreviousScore(trend.getScore()));
    }

    /**
     * 최근 트렌드 조회
     */
    public List<RecentTrendResponse> getRecentTrends(){
        LocalDateTime latestPeriod = recentTrendRepository.findLatestPeriod();
        if(latestPeriod==null){
            return Collections.emptyList();
        }
        List<RecentTrend> recentTrends =recentTrendRepository.findTop3ByPeriodOrderByIncreaseScoreDesc(latestPeriod);
        return recentTrends.stream()
                .map(RecentTrendResponse::from)
                .toList();
    }

    /**
     * 댓글 생성
     */
    @Transactional
    public void addComment(Long trendId, UUID userId, String content){
        Trend trend = trendRepository.findById(trendId)
                .orElseThrow(TrendNotFoundException::new); //TREND-002

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new); //USER-001

        TrendComment comment = TrendComment.of(user,trend,content);

        trend.increaseCommentCount();

        trendCommentRepository.save(comment);
    }
    /**
     * 댓글 삭제
     */
    @Transactional
    public void deleteComment(Long commentId, UUID userId) {
        TrendComment comment = trendCommentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new); //TREND-007

        if(!comment.getUser().getId().equals(userId)){
            throw new UserAccessDeniedException(); //USER-012
        }

        comment.getTrend().decreaseCommentCount();
        trendCommentRepository.delete(comment);
    }

    /**
     * 댓글 좋아요
     */
    @Transactional
    public void likeComment(Long commentId, UUID userId) {
        TrendComment comment = trendCommentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);//TREND-007

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new); //USER-001

        Optional<TrendCommentLike> existing=trendCommentLikeReposity.findByUserAndComment(user,comment);

        if(existing.isPresent()){
            trendCommentLikeReposity.delete(existing.get());
            comment.decreaseLikeCount();
        }else{
            TrendCommentLike like=TrendCommentLike.of(user,comment);
            trendCommentLikeReposity.save(like);
            comment.increaseLikeCount();
        }
    }

    /**
     * 트렌드 분석 조회
     */
    public TrendStatisticsResponse getTrendStatistics(Long trendId){
        Trend trend = trendRepository.findById(trendId)
                .orElseThrow(TrendNotFoundException::new); //TREND-002

        int postCount=postRepository.countByTrendAndDeletedFalse(trend);
        int relatedPostLikeCount = postRepository.sumLikesByTrend(trend);

        // 임의값
        int snsMentionCount = ThreadLocalRandom.current().nextInt(30, 100);
        int searchCount = ThreadLocalRandom.current().nextInt(50, 150);

        return TrendStatisticsResponse.from(
                trend,
                postCount,
                relatedPostLikeCount,
                snsMentionCount,
                searchCount
        );
    }

    /**
     * 트렌드 검색
     */
    public TrendSearchPagedResponse searchTrends(TrendSearchCondition condition, Pageable pageable) {
        Page<Trend> trends = trendRepository.searchAll(condition, pageable);

        List<TrendListResponse> responseList = trends.getContent().stream()
                .map(TrendListResponse::from)
                .toList();

        return TrendSearchPagedResponse.from(responseList, trends);
    }

    /**
     * 스크랩된 트렌드 검색
     */
    public TrendSearchPagedResponse searchScrappedTrends(TrendSearchCondition condition, Pageable pageable) {
        Page<Trend> trends = trendRepository.searchScrapped(condition, pageable);

        List<TrendListResponse> responseList = trends.getContent().stream()
                .map(TrendListResponse::from)
                .toList();

        return TrendSearchPagedResponse.from(responseList, trends);
    }

    /**
     * 피크타임 저장
     */
    @Transactional
    public void updatePeakPeriods() {
        try {
            LocalDate now = LocalDate.now();
            LocalDate startOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
            LocalDate endOfLastMonth = now.withDayOfMonth(2).minusDays(1);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월");

            List<Trend> trends = trendRepository.findAll();

            for (Trend trend : trends) {
                int postCount = postRepository.countByTrendAndCreatedAtBetweenAndDeletedFalse(
                        trend,
                        startOfLastMonth.atStartOfDay(),
                        endOfLastMonth.atTime(23, 59, 59)
                );

                if (postCount >= 50) {
                    String formattedPeriod = startOfLastMonth.format(formatter);  // "2025년 8월"
                    trend.setPeakPeriod(formattedPeriod);
                }
            }
        } catch (Exception e) {
            throw new AppException(TrendErrorCode.PEAK_PERIOD_UPDATE_FAIL);
        }
    }

    /**
     * 검색량
     */
    @Transactional
    public void increaseSearchVolume(String keyword) {
        trendRepository.findByTitle(keyword)
                .ifPresent(Trend::increaseSearchVolume);
    }

}
