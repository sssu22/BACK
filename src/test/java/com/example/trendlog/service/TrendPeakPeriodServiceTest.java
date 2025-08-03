package com.example.trendlog.service;

import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.repository.post.PostRepository;
import com.example.trendlog.repository.trend.TrendRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrendPeakPeriodServiceTest {
    @InjectMocks
    private TrendService trendService;

    @Mock
    private TrendRepository trendRepository;

    @Mock
    private PostRepository postRepository;

    @Test
    void 피크시기를_갱신한다() {
        // given
        Trend trend = Trend.builder()
                .title("테스트 트렌드")
                .peakPeriod("미정")
                .build();

        List<Trend> trends = List.of(trend);

        LocalDate now = LocalDate.of(2025, 8, 1);
        LocalDate start = now.minusMonths(1).withDayOfMonth(1); // 2025-07-01
        LocalDateTime startTime = start.atStartOfDay();
        LocalDateTime endTime = now.withDayOfMonth(1).minusDays(1).atTime(23, 59, 59); // 2025-07-31 23:59:59

        when(trendRepository.findAll()).thenReturn(trends);
        when(postRepository.countByTrendAndCreatedAtBetweenAndDeletedFalse(trend, startTime, endTime))
                .thenReturn(75); // mock으로 75개 게시글 있다고 가정

        // when
        trendService.updatePeakPeriods();

        // then
        assertEquals("2025년 7월", trend.getPeakPeriod());
    }

    @Test
    void 게시글_수가_50미만이면_피크시기를_변경하지_않는다() {
        // given
        Trend trend = Trend.builder().peakPeriod("미정").build();
        when(trendRepository.findAll()).thenReturn(List.of(trend));
        when(postRepository.countByTrendAndCreatedAtBetweenAndDeletedFalse(any(), any(), any())).thenReturn(30);

        // when
        trendService.updatePeakPeriods();

        // then
        assertEquals("미정", trend.getPeakPeriod());
    }

}
