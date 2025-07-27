package com.example.trendlog.service;

import com.example.trendlog.domain.User;
import com.example.trendlog.domain.post.*;
import com.example.trendlog.domain.trend.Trend;
import com.example.trendlog.dto.request.post.PostCreateUpdateRequest;
import com.example.trendlog.dto.response.post.*;
import com.example.trendlog.global.exception.AppException;
import com.example.trendlog.global.exception.trend.TrendNotFoundException;
import com.example.trendlog.repository.UserRepository;
import com.example.trendlog.repository.post.*;
import com.example.trendlog.repository.trend.TrendRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.trendlog.global.exception.code.PostErrorCode.POST_NOT_FOUND;
import static com.example.trendlog.global.exception.code.PostErrorCode.POST_NOT_WRITER;
import static com.example.trendlog.global.exception.code.UserErrorCode.USER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostScrapRepository postScrapRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final PostStatisticsRepository postStatisticsRepository;
    private final PostCommentService postCommentService;
    private final KakaoLocalService kakaoLocalService;
    private final TrendRepository trendRepository;
    private final JPAQueryFactory queryFactory;


    // 게시글 생성
    public void createPost(Principal principal, PostCreateUpdateRequest request) {
        User user = findUser(principal);
        request.validate(); // 요청 유효성 검사
        String district = kakaoLocalService.getDistrictByCoordinates(request.getLatitude(), request.getLongitude());
        Trend trend = trendRepository.findById(request.getTrendId())
                .orElseThrow(TrendNotFoundException::new);
        Post post = request.toEntity(user, district, trend);
        // 이미 존재하는 태그인지 확인, 존재하는 태그이면 PostTag 객체에 추가, 존재하지 않는 태그이면 Tag 객체 생성 후 저장
        List<String> tags = request.getTags();
        int sortOrder = 1;
        for(String tagName : tags) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = Tag.of(tagName);
                        return tagRepository.save(newTag);
                    });
            PostTag postTag = PostTag.of(post, tag, sortOrder++);
            post.getTags().add(postTag);
        }
        postRepository.save(post);
    }

    // 게시글 목록 조회(페이징)
    public PostPagedResponse getPostList(String sortBy, String emotion, int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size, getSort(sortBy));
        Page<Post> postPage;

        if (emotion.equalsIgnoreCase("ALL")) {
            postPage = postRepository.findAllByDeletedFalse(pageable);
        } else {
            Emotion emotionType = Emotion.from(emotion);
            postPage = postRepository.findAllByDeletedFalseAndEmotion(emotionType, pageable);
        }

        // 응답 변환
        List<PostListResponse> list = postPage.getContent().stream()
                .map(PostListResponse::from)
                .toList();

        return PostPagedResponse.from(list, postPage);
    }

    // 게시글 상세 조회

    public PostResponse getPostDetail(Principal principal, Long postId) {
        Post post = getPost(postId);
        post.addViewCount();

        UUID userId = null;
        boolean isLiked = false;
        boolean isScrapped = false;

        if (principal != null) {
            User user = findUser(principal);
            userId = user.getId();
            isLiked = postLikeRepository.existsByUserIdAndPostId(userId, postId);
            isScrapped = postScrapRepository.existsByUserIdAndPostId(userId, postId);
        }

        return PostResponse.from(post, isLiked, isScrapped, postCommentService.getPostCommentList(postId));
    }

    // 인기 게시글 목록 조회
    // 좋아요 + 스크랩 수 기준으로 정렬
    // 지금 뜨는 경험
    // 일주일 내로 만들어진 게시글 중에서 좋아요 + 스크랩 수 높은 순 자정마다 업데이트
    public PostPopularPagedResponse getPopularPostList(int page, int size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<PostStatistics> posts = postStatisticsRepository.findAllOrderByTotalCountDescPostCreatedAtDesc(pageable);
        List<PostPopularListResponse> list = posts.getContent().stream()
                .map(PostPopularListResponse::from)
                .toList();
        return PostPopularPagedResponse.from(list, posts);
    }

    // 게시글 수정
    public void updatePost(Principal principal, Long postId, PostCreateUpdateRequest request) {
        User user = findUser(principal);
        Post post = getPost(postId);

        if (!post.getUser().getId().equals(user.getId())) { // 게시글 작성자와 요청한 사용자가 다를 경우
            throw new AppException(POST_NOT_WRITER);
        }

        request.validate(); // 요청 유효성 검사
        String district = kakaoLocalService.getDistrictByCoordinates(request.getLatitude(), request.getLongitude());
        Trend trend = trendRepository.findById(request.getTrendId())
                .orElseThrow(TrendNotFoundException::new);
        post.update(request, district, trend);
        // 기존 태그 삭제 후 새로 추가
        // PostTag 엔티티는 cascade = CascadeType.ALL로 설정되어 있어, Post 엔티티에서 태그를 제거하면 자동으로 삭제됨
        post.getTags().clear();
        int sortOrder = 1;
        for(String tagName : request.getTags()) {
            Tag tag = tagRepository.findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = Tag.of(tagName);
                        return tagRepository.save(newTag);
                    });
            PostTag postTag = PostTag.of(post, tag, sortOrder++);
            post.getTags().add(postTag);
        }
    }


    // 게시글 삭제
    public void deletePost(Principal principal, Long postId) {
        User user = findUser(principal);
        Post post = getPost(postId);

        if (!post.getUser().getId().equals(user.getId())) { // 게시글 작성자와 요청한 사용자가 다를 경우
            throw new AppException(POST_NOT_WRITER);
        }
        post.changeIsDeleted();
    }

    // 게시글 좋아요
    public void likePost(Principal principal, Long postId) {
        User user = findUser(principal);
        Post post = getPost(postId);

        if(postLikeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            postLikeRepository.deleteByUserIdAndPostId(user.getId(), postId);
            post.changeLikeCount(-1);
        } else {
            postLikeRepository.save(PostLike.of(user, post));
            post.changeLikeCount(+1);
        }
    }

    // 게시글 스크랩
    public void scrapPost(Principal principal, Long postId) {
        User user = findUser(principal);
        Post post = getPost(postId);

        if(postScrapRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            postScrapRepository.deleteByUserIdAndPostId(user.getId(), postId);
            post.changeScrapCount(-1);
        } else {
            postScrapRepository.save(PostScrap.of(user, post));
            post.changeScrapCount(+1);
        }
    }

    // 내 게시글 지도 위치에 표기하기
    public List<PostMapResponse> getPostMarkersInArea(Principal principal) {
        User user = findUser(principal);

        // 구별 게시글 수 조회
        List<Object[]> results = postRepository.countPostsByDistrictForUser(user.getId());

        // 변환
        return results.stream()
                .map(row -> {
                    String district = (String) row[0];
                    Long count = (Long) row[1]; // JPA에서는 count가 Long으로 나옴
                    return PostMapResponse.from(district, count.intValue());
                })
                .toList();
    }

    // 내 게시글 조회
    public PostPagedResponse getMyPostList(Principal principal, String district, int page, int size) {
        User user = findUser(principal);
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Post> postPage;

        if (district.equalsIgnoreCase("all")) {
            postPage = postRepository.findAllByDeletedFalseAndUserId(user.getId(), pageable);
        } else {
            postPage = postRepository.findAllByDeletedFalseAndUserIdAndDistrict(user.getId(), district, pageable);
        }

        List<PostListResponse> list = postPage.getContent().stream()
                .map(PostListResponse::from)
                .toList();

        return PostPagedResponse.from(list, postPage);
    }



    private Post getPost(Long postId) {
        return postRepository.findByIdAndDeletedFalse(postId)
                .orElseThrow(() -> new AppException(POST_NOT_FOUND));
    }

    public User findUser(Principal principal) {
        return userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new AppException(USER_NOT_FOUND));
    }

    private Sort getSort(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "trend" -> Sort.by(Sort.Direction.DESC, "likeCount"); // 우선 좋아요 순으로 해놓겠음
            case "latest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }


    // 태그 조합해서 게시글 검색
    public PostPagedResponse searchAllPosts(PostSearchCondition condition, Pageable pageable) {
        Page<Post> posts = postRepository.searchAll(condition, pageable);
        List<Long> postIds = posts.getContent().stream()
                .map(Post::getId)
                .toList();

        if (postIds.isEmpty()) {
            return PostPagedResponse.from(List.of(), posts);
        }

        QPost post = QPost.post;
        QPostTag postTag = QPostTag.postTag;
        QTag tag = QTag.tag;

        Map<Long, List<String>> tagMap = queryFactory
                .select(post.id, tag.name)
                .from(post)
                .join(post.tags, postTag)
                .join(postTag.tag, tag)
                .where(post.id.in(postIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> Objects.requireNonNull(tuple.get(post.id)),
                        Collectors.mapping(tuple -> Objects.requireNonNull(tuple.get(tag.name)), Collectors.toList())
                ));

        List<PostListResponse> responseList = posts.getContent().stream()
                .map(p -> PostListResponse.from(p, tagMap.getOrDefault(p.getId(), List.of())))
                .toList();

        return PostPagedResponse.from(responseList, posts);
    }

    // 내 게시글 검색
    public PostPagedResponse searchMyPosts(PostSearchCondition condition, Pageable pageable) {
        // 내가 쓴 게시글만 검색
        Page<Post> posts = postRepository.searchMy(condition, pageable);

        List<Long> postIds = posts.getContent().stream()
                .map(Post::getId)
                .toList();

        if (postIds.isEmpty()) {
            return PostPagedResponse.from(List.of(), posts);
        }

        QPost post = QPost.post;
        QPostTag postTag = QPostTag.postTag;
        QTag tag = QTag.tag;

        // 게시글 ID에 해당하는 전체 태그 조회
        Map<Long, List<String>> tagMap = queryFactory
                .select(post.id, tag.name)
                .from(post)
                .join(post.tags, postTag)
                .join(postTag.tag, tag)
                .where(post.id.in(postIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> Objects.requireNonNull(tuple.get(post.id)),
                        Collectors.mapping(tuple -> Objects.requireNonNull(tuple.get(tag.name)), Collectors.toList())
                ));

        List<PostListResponse> responseList = posts.getContent().stream()
                .map(p -> PostListResponse.from(p, tagMap.getOrDefault(p.getId(), List.of())))
                .toList();

        return PostPagedResponse.from(responseList, posts);
    }

    // 내가 스크랩한 게시글
    public PostPagedResponse searchScrappedPosts(PostSearchCondition condition, Pageable pageable) {
        Page<Post> posts = postRepository.searchScrapped(condition, pageable);

        List<Long> postIds = posts.getContent().stream()
                .map(Post::getId)
                .toList();

        if (postIds.isEmpty()) {
            return PostPagedResponse.from(List.of(), posts);
        }

        Map<Long, List<String>> tagMap = queryFactory
                .select(QPost.post.id, QTag.tag.name)
                .from(QPost.post)
                .join(QPost.post.tags, QPostTag.postTag)
                .join(QPostTag.postTag.tag, QTag.tag)
                .where(QPost.post.id.in(postIds))
                .fetch()
                .stream()
                .collect(Collectors.groupingBy(
                        tuple -> tuple.get(QPost.post.id),
                        Collectors.mapping(tuple -> tuple.get(QTag.tag.name), Collectors.toList())
                ));

        List<PostListResponse> responseList = posts.getContent().stream()
                .map(p -> PostListResponse.from(p, tagMap.getOrDefault(p.getId(), List.of())))
                .toList();

        return PostPagedResponse.from(responseList, posts);
    }
}
