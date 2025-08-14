package com.example.trendlog.repository.post;

import com.example.trendlog.domain.post.*;
import com.example.trendlog.domain.trend.QTrend;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> searchAll(PostSearchCondition condition, Pageable pageable) {
        QPost post = QPost.post;
        QTrend trend = QTrend.trend;
        QPostTag postTag = QPostTag.postTag;
        QTag tag = QTag.tag;

        // 1. Post만 검색 (태그 조건은 필터로만 사용)
        List<Post> content = queryFactory
                .selectFrom(post)
                .leftJoin(post.trend, trend).fetchJoin()
                .leftJoin(post.tags, postTag)
                .leftJoin(postTag.tag, tag)
                .where(
                        keywordMatch(condition.getKeyword(), post, tag, trend),
                        emotionMatch(condition.getEmotion(), post),
                        post.deleted.eq(false) // 삭제되지 않은 것만
                )
                .orderBy(getSortOrder(pageable, post, trend))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();


        // 2. 전체 개수 카운트
        Long total = queryFactory
                .select(post.countDistinct())
                .from(post)
                .leftJoin(post.trend, trend)
                .leftJoin(post.tags, postTag)
                .leftJoin(postTag.tag, tag)
                .where(
                        keywordMatch(condition.getKeyword(), post, tag, trend),
                        emotionMatch(condition.getEmotion(), post),
                        post.deleted.eq(false) // 삭제되지 않은 것만
                )
                .fetchOne();
        long safeTotal = total != null ? total : 0L;

        return new PageImpl<>(content, pageable, safeTotal);
    }

    @Override
    public Page<Post> searchMy(PostSearchCondition condition, Pageable pageable) {
        QPost post = QPost.post;
        QTrend trend = QTrend.trend;
        QPostTag postTag = QPostTag.postTag;
        QTag tag = QTag.tag;

        List<Post> content = queryFactory
                .selectFrom(post)
                .leftJoin(post.trend, trend).fetchJoin()
                .leftJoin(post.tags, postTag)
                .leftJoin(postTag.tag, tag)
                .where(
                        post.user.id.eq(condition.getUserId()), // 작성자 필터
                        keywordMatch(condition.getKeyword(), post, tag, trend),
                        emotionMatch(condition.getEmotion(), post),
                        post.deleted.eq(false) // 삭제되지 않은 것만
                )
                .orderBy(getSortOrder(pageable, post, trend))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        Long total = queryFactory
                .select(post.countDistinct())
                .from(post)
                .leftJoin(post.trend, trend)
                .leftJoin(post.tags, postTag)
                .leftJoin(postTag.tag, tag)
                .where(
                        post.user.id.eq(condition.getUserId()), // 작성자 필터
                        keywordMatch(condition.getKeyword(), post, tag, trend),
                        emotionMatch(condition.getEmotion(), post),
                        post.deleted.eq(false) // 삭제되지 않은 것만
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    @Override
    public Page<Post> searchScrapped(PostSearchCondition condition, Pageable pageable) {
        QPost post = QPost.post;
        QTrend trend = QTrend.trend;
        QPostTag postTag = QPostTag.postTag;
        QTag tag = QTag.tag;
        QPostScrap scrap = QPostScrap.postScrap;

        List<Post> content = queryFactory
                .selectFrom(post)
                .leftJoin(post.trend, trend).fetchJoin()
                .leftJoin(post.tags, postTag)
                .leftJoin(postTag.tag, tag)
                .join(scrap).on(scrap.post.eq(post))
                .where(
                        scrap.user.id.eq(condition.getUserId()), // 스크랩한 게시글 필터
                        keywordMatch(condition.getKeyword(), post, tag, trend),
                        emotionMatch(condition.getEmotion(), post),
                        post.deleted.eq(false) // 삭제되지 않은 것만
                )
                .orderBy(getSortOrder(pageable, post, trend))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .distinct()
                .fetch();

        // count 쿼리
        Long total = queryFactory
                .select(post.countDistinct())
                .from(post)
                .leftJoin(post.tags, postTag)
                .leftJoin(postTag.tag, tag)
                .leftJoin(post.trend, trend)
                .join(scrap).on(scrap.post.eq(post))
                .where(
                        scrap.user.id.eq(condition.getUserId()),
                        keywordMatch(condition.getKeyword(), post, tag, trend),
                        emotionMatch(condition.getEmotion(), post),
                        post.deleted.eq(false) // 삭제되지 않은 것만
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }


    // 키워드 검색 조건: 제목, 내용, 장소
    private BooleanExpression keywordMatch(String keyword, QPost post, QTag tag, QTrend trend) {
        if (!StringUtils.hasText(keyword)) return null;

        return post.title.containsIgnoreCase(keyword)
                .or(post.description.containsIgnoreCase(keyword))
                .or(post.location.containsIgnoreCase(keyword))
                .or(tag.name.containsIgnoreCase(keyword))
                .or(trend.title.containsIgnoreCase(keyword));
    }

    // 감정 필터
    private BooleanExpression emotionMatch(String emotion, QPost post) {
        if (!StringUtils.hasText(emotion) || emotion.equals("all")) return null;
        return post.emotion.stringValue().eq(emotion);
    }

    // 정렬 조건 (latest, trend)
    private OrderSpecifier<?>[] getSortOrder(Pageable pageable, QPost post, QTrend trend) {
        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            boolean asc = order.isAscending();

            return switch (property) {
                case "createdAt" -> new OrderSpecifier[]{
                        asc ? post.createdAt.asc() : post.createdAt.desc()
                };
                case "trendScore" -> new OrderSpecifier[]{
                        asc ? trend.score.asc() : trend.score.desc(),
                        post.createdAt.desc() // 같은 점수면 최신순
                };
                default -> new OrderSpecifier[]{post.createdAt.desc()};
            };
        }
        return new OrderSpecifier[]{post.createdAt.desc()};
    }

}
