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
import com.example.trendlog.repository.trend.*;
import com.example.trendlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final RecentTrendRepository recentTrendRepository;
    private final TrendCommentRepository trendCommentRepository;
    private final TrendCommentLikeReposity trendCommentLikeReposity;

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
                .map(TrendCommentDto::from)
                .collect(Collectors.toList());

        boolean isLiked = trendLikeRepository.existsByTrendAndUser(trend, user);
        boolean isScrapped = trendScrapRepository.existsByTrendAndUser(trend, user);

        return TrendDetailResponse.from(trend,comments,isLiked,isScrapped);
    }

    /**
     * 트렌드 목록
     */
    public TrendListPageResponse getTrendList(Pageable pageable) {
        if (pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new AppException(TrendErrorCode.INVALID_PAGE_REQUEST);
        }
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

}
