package com.example.trendlog.service;

import com.example.trendlog.repository.post.PostStatisticsRepository;
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
public class PostStatisticsScheduler {
    private final PostStatisticsRepository postStatisticsRepository;

    // 애플리케이션 시작 시 게시글 통계 초기화
    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void initStatistics() {
        updatePostStatistics();
        log.info("애플리케이션 시작 시 게시글 통계 초기화 완료");
    }


    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updatePostStatistics() {

        postStatisticsRepository.deleteAllStatistics();
        postStatisticsRepository.updateCommunityStatistics();
        log.info("게시글 통계 업데이트 완료.");
    }
}
