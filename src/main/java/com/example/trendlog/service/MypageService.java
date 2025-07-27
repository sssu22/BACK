package com.example.trendlog.service;

import com.example.trendlog.domain.User;
import com.example.trendlog.domain.post.Post;
import com.example.trendlog.dto.response.mypage.ProfileResponse;
import com.example.trendlog.dto.response.mypage.RecentPostActivityResponse;
import com.example.trendlog.dto.response.mypage.UserStatisticsResponse;
import com.example.trendlog.dto.response.post.PostListResponse;
import com.example.trendlog.dto.response.trend.TrendListResponse;
import com.example.trendlog.global.exception.user.UserNotFoundException;
import com.example.trendlog.repository.UserRepository;
import com.example.trendlog.repository.post.PostRepository;
import com.example.trendlog.repository.post.PostScrapRepository;
import com.example.trendlog.repository.trend.TrendScrapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final TrendScrapRepository trendScrapRepository;
    private final PostScrapRepository postScrapRepository;

    //내 활동 통계 요약
    public UserStatisticsResponse getUserStatistics(UUID userId) {
        User user =findUser(userId);//USER-001

        int postCount=postRepository.countByUserAndDeletedFalse(user);
        Integer avgScore=postRepository.findAverageTrendScoreByUser(user);
        int averageScore=(avgScore==null)?0:avgScore;
        int visitPlaceCount=postRepository.countDistinctDistrictsByUser(user);
        int scrapCount=postScrapRepository.countValidScrapByUser(user)+trendScrapRepository.countByUser(user);

        return UserStatisticsResponse.from(postCount,averageScore,visitPlaceCount,scrapCount);
    }

    //내 최근 활동
    public List<RecentPostActivityResponse> getRecentPostActivity(UUID userId) {
        User user =findUser(userId);//USER-001

        return postRepository.findTop3ByUserAndDeletedFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(RecentPostActivityResponse::from)
                .toList();
    }

    //내가 스크랩한 게시글
    public List<PostListResponse> getMyScrapPostList(UUID userId) {
        User user =findUser(userId);
        return postScrapRepository.findByUserAndPostDeletedFalseOrderByCreatedAtDesc(user)
                .stream()
                .map(postScrap -> PostListResponse.from(postScrap.getPost()))
                .toList();
    }

    //내가 스트랩한 트렌드
    public List<TrendListResponse> getMyScrapTrendList(UUID userId) {
        User user =findUser(userId);
        return trendScrapRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(trendScrap -> TrendListResponse.from(trendScrap.getTrend()))
                .toList();
    }

    //마이페이지 상단 정보 조회
    public ProfileResponse getMyProfile(UUID userId) {
        User user =findUser(userId);
        return ProfileResponse.from(user);
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new); //USER-001
    }
}
