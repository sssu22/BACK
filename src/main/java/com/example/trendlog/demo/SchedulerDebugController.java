package com.example.trendlog.demo;

import com.example.trendlog.service.TrendService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 스케줄러 강제 실행 코드
 */
@Tag(name = "Scheduler", description = "스케쥴러를 강제 실행하기 위한 API(테스트용)")
@RestController
@RequestMapping("/debug/scheduler")
@RequiredArgsConstructor
public class SchedulerDebugController {
    private final TrendService trendService;
    @PostMapping("/popular")
    public ResponseEntity<Void> runPopularTrendJob() {
        trendService.updatePopularTrends();
        return ResponseEntity.ok().build();
    }
    @PostMapping("/recent-trends")
    public ResponseEntity<String> runRecentTrendScheduler() {
        trendService.updateRecentTrends();
        return ResponseEntity.ok("최근 트렌드 스케줄러 실행 완료");
    }
}
