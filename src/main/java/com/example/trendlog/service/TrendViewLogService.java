package com.example.trendlog.service;

import com.example.trendlog.domain.User;
import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.domain.trend.TrendViewLog;
import com.example.trendlog.global.exception.trend.TrendNotFoundException;
import com.example.trendlog.repository.trend.TrendRepository;
import com.example.trendlog.repository.trend.TrendViewLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TrendViewLogService {
    private final TrendRepository trendRepository;
    private final TrendViewLogRepository trendViewLogRepository;

    @Transactional
    public void logTrendView(User user, Long trendId) {
        Trend trend = trendRepository.findById(trendId)
                .orElseThrow(TrendNotFoundException::new);

        trendViewLogRepository.save(new TrendViewLog(user, trend));
    }
}
