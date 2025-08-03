package com.example.trendlog.service;

import com.example.trendlog.domain.User;
import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.UserTrendRecommendation;
import com.example.trendlog.dto.response.trend.RecommendedTrendResponse;
import com.example.trendlog.global.exception.trend.TrendNotFoundException;
import com.example.trendlog.global.exception.user.UserNotFoundException;
import com.example.trendlog.repository.UserRepository;
import com.example.trendlog.repository.trend.TrendRepository;
import com.example.trendlog.repository.trend.TrendScrapRepository;
import com.example.trendlog.repository.trend.UserTrendRecommendationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecommendationService {

    private final UserRepository userRepository;
    private final UserTrendRecommendationRepository userTrendRecommendationRepository;
    private final TrendRepository trendRepository;
    private final TrendScrapRepository trendScrapRepository;

    @Transactional
    public void saveRecommendations(UUID userId, List<Long> trendIds){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        userTrendRecommendationRepository.deleteByUser(user);

        for(int i=0; i<trendIds.size(); i++){
            Trend trend = trendRepository.findById(trendIds.get(i))
                    .orElseThrow(TrendNotFoundException::new);
            UserTrendRecommendation recommendation=new UserTrendRecommendation(user,trend,i+1);
            userTrendRecommendationRepository.save(recommendation);
        }
    }

    public List<RecommendedTrendResponse> getRecommendations(User user){
        List<UserTrendRecommendation> recommendations=userTrendRecommendationRepository.findTop3ByUserOrderByRankAsc(user);

        return recommendations.stream()
                .map(rec->{
                    Trend trend=rec.getTrend();
                    boolean isScrapped = (user!=null)&&trendScrapRepository.existsByTrendAndUser(trend,user);
                    return RecommendedTrendResponse.from(trend,isScrapped);
                })
                .toList();



    }

}
