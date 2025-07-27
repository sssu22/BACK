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
                        keywordMatch(condition.getKeyword(), post, tag),
                        emotionMatch(condition.getEmotion(), post)
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
                        keywordMatch(condition.getKeyword(), post, tag),
                        emotionMatch(condition.getEmotion(), post)
                )
                .fetchOne();
        long safeTotal = total != null ? total : 0L;

        return new PageImpl<>(content, pageable, safeTotal);
    }



    // 키워드 검색 조건: 제목, 내용, 장소
    private BooleanExpression keywordMatch(String keyword, QPost post, QTag tag) {
        if (!StringUtils.hasText(keyword)) return null;

        return post.title.containsIgnoreCase(keyword)
                .or(post.description.containsIgnoreCase(keyword))
                .or(post.location.containsIgnoreCase(keyword))
                .or(tag.name.containsIgnoreCase(keyword));
    }

    // 감정 필터
    private BooleanExpression emotionMatch(String emotion, QPost post) {
        if (!StringUtils.hasText(emotion) || emotion.equals("전체")) return null;
        return post.emotion.stringValue().eq(emotion);
    }

    // 정렬 조건 (latest, trend)
    private OrderSpecifier<?> getSortOrder(Pageable pageable, QPost post, QTrend trend) {
        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            boolean asc = order.isAscending();

            return switch (property) {
                case "createdAt" -> asc ? post.createdAt.asc() : post.createdAt.desc();
                case "trendScore" -> asc ? trend.score.asc() : trend.score.desc();
                default -> post.createdAt.desc();
            };
        }
        return post.createdAt.desc();
    }
}
