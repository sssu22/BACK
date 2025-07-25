package com.example.trendlog.service;

import com.example.trendlog.repository.post.PostStatisticsRepository;
import com.example.trendlog.repository.post.TagStatisticsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TagStatisticsScheduler {
    private final TagStatisticsRepository tagStatisticsRepository;

    // 애플리케이션 시작 시 게시글 통계 초기화
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initStatistics() {
        updateTagStatistics();
        log.info("애플리케이션 시작 시 태그 통계 초기화 완료");
    }


    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateTagStatistics() {
        tagStatisticsRepository.upsertAllStatistics();
        log.info("태그 통계 UPSERT 완료.");
    }
}
