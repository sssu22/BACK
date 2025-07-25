package com.example.trendlog.service;

import com.example.trendlog.domain.post.TagStatistics;
import com.example.trendlog.dto.response.post.TagPopularListResponse;
import com.example.trendlog.dto.response.post.TagPopularPagedResponse;
import com.example.trendlog.repository.post.TagStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TagService {
    private final TagStatisticsRepository tagStatisticsRepository;

    // 인기 태그 조회
    public TagPopularPagedResponse getPopularTagList(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<TagStatistics> tagStatistics = tagStatisticsRepository.findAllOrderByTotalCountDescTagCreatedAtDesc(pageable);

        int startRank = (page - 1) * size + 1;

        List<TagPopularListResponse> list = IntStream.range(0, tagStatistics.getContent().size())
                .mapToObj(i -> TagPopularListResponse.from(tagStatistics.getContent().get(i), startRank + i))
                .toList();
        return TagPopularPagedResponse.from(list, tagStatistics);
    }
}
