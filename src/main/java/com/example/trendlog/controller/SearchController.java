package com.example.trendlog.controller;

import com.example.trendlog.domain.user.User;
import com.example.trendlog.domain.post.PostSearchCondition;
import com.example.trendlog.domain.trend.TrendSearchCondition;
import com.example.trendlog.dto.response.post.PostPagedResponse;
import com.example.trendlog.dto.response.trend.TrendSearchPagedResponse;
import com.example.trendlog.global.docs.SearchSwaggerSpec;
import com.example.trendlog.global.dto.DataResponse;
import com.example.trendlog.service.post.PostService;
import com.example.trendlog.service.trend.TrendService;
import com.example.trendlog.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController implements SearchSwaggerSpec {

    private final PostService postService;
    private final TrendService trendService;
    private final UserService userService;

    // 게시글에서 게시글 이름, 게시글 내용, 게시글 태그, 장소 중에서 해당하는 거 검색
    @GetMapping("/posts")
    public ResponseEntity<DataResponse<PostPagedResponse>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String emotion,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy
    ) {
        PostSearchCondition condition = new PostSearchCondition();
        condition.setKeyword(keyword);
        condition.setEmotion(emotion);

        Sort sort = switch (sortBy) {
            case "trend" -> Sort.by(Sort.Direction.DESC, "trendScore"); // 커스텀 키
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Pageable pageable = PageRequest.of(page-1, size, sort);
        PostPagedResponse postPagedResponse = postService.searchAllPosts(condition, pageable);
        return ResponseEntity.ok(DataResponse.from(postPagedResponse));
    }

    // 내 게시글 검색
    @GetMapping("/posts/my")
    public ResponseEntity<DataResponse<PostPagedResponse>> searchMyPosts(
            Principal principal,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String emotion,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy
    ) {
        User user = userService.findUser(principal);

        PostSearchCondition condition = new PostSearchCondition();
        condition.setKeyword(keyword);
        condition.setEmotion(emotion);
        condition.setUserId(user.getId());

        Sort sort = switch (sortBy) {
            case "trend" -> Sort.by(Sort.Direction.DESC, "trendScore"); // 커스텀 키
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Pageable pageable = PageRequest.of(page-1, size, sort);
        PostPagedResponse postPagedResponse = postService.searchMyPosts(condition, pageable);
        return ResponseEntity.ok(DataResponse.from(postPagedResponse));
    }

    @GetMapping("/posts/scrap")
    public ResponseEntity<DataResponse<PostPagedResponse>> searchScrappedPosts(
            Principal principal,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String emotion,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy
    ) {
        User user = userService.findUser(principal);

        PostSearchCondition condition = new PostSearchCondition();
        condition.setKeyword(keyword);
        condition.setEmotion(emotion);
        condition.setUserId(user.getId());

        Sort sort = switch (sortBy) {
            case "trend" -> Sort.by(Sort.Direction.DESC, "trendScore");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        PostPagedResponse postPagedResponse = postService.searchScrappedPosts(condition, pageable);
        return ResponseEntity.ok(DataResponse.from(postPagedResponse));
    }

    @GetMapping("/trends")
    public ResponseEntity<DataResponse<TrendSearchPagedResponse>> searchTrends(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy // trend, latest
    ) {
        trendService.increaseSearchVolume(keyword);
        TrendSearchCondition condition = new TrendSearchCondition();
        condition.setKeyword(keyword);
        condition.setCategory(category);

        Sort sort = switch (sortBy) {
            case "trend" -> Sort.by(Sort.Direction.DESC, "trendScore");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Pageable pageable = PageRequest.of(page-1, size, sort);
        TrendSearchPagedResponse trendSearchPagedResponse = trendService.searchTrends(condition, pageable);
        return ResponseEntity.ok(DataResponse.from(trendSearchPagedResponse));
    }

    @GetMapping("/trends/scrap")
    public ResponseEntity<DataResponse<TrendSearchPagedResponse>> searchScrappedTrends(
            Principal principal,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "all") String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "latest") String sortBy // trend, latest
    ) {
        User user = userService.findUser(principal);

        TrendSearchCondition condition = new TrendSearchCondition();
        condition.setKeyword(keyword);
        condition.setCategory(category);
        condition.setUserId(user.getId());

        Sort sort = switch (sortBy) {
            case "trend" -> Sort.by(Sort.Direction.DESC, "trendScore");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };

        Pageable pageable = PageRequest.of(page-1, size, sort);
        TrendSearchPagedResponse trendSearchPagedResponse = trendService.searchScrappedTrends(condition, pageable);
        return ResponseEntity.ok(DataResponse.from(trendSearchPagedResponse));
    }




}
