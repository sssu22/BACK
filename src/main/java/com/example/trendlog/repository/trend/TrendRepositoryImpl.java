package com.example.trendlog.repository.trend;

import com.example.trendlog.domain.trend.*;
import com.example.trendlog.global.exception.AppException;
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
public class TrendRepositoryImpl implements TrendRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Trend> searchAll(TrendSearchCondition condition, Pageable pageable) {
        QTrend trend = QTrend.trend;

        List<Trend> content = queryFactory
                .selectFrom(trend)
                .where(
                        titleOrCategoryMatch(condition.getKeyword(), trend),
                        categoryEquals(condition.getCategory(), trend)
                )
                .orderBy(getSortOrder(pageable, trend))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(trend.count())
                .from(trend)
                .where(
                        titleOrCategoryMatch(condition.getKeyword(), trend),
                        categoryEquals(condition.getCategory(), trend)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    public Page<Trend> searchScrapped(TrendSearchCondition condition, Pageable pageable) {
        QTrend trend = QTrend.trend;
        QTrendScrap scrap = QTrendScrap.trendScrap;

        List<Trend> content = queryFactory
                .selectFrom(trend)
                .join(scrap).on(scrap.trend.eq(trend))
                .where(
                        scrap.user.id.eq(condition.getUserId()),
                        titleOrCategoryMatch(condition.getKeyword(), trend),
                        categoryEquals(condition.getCategory(), trend)
                )
                .orderBy(getSortOrder(pageable, trend))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(trend.count())
                .from(trend)
                .join(scrap).on(scrap.trend.eq(trend))
                .where(
                        scrap.user.id.eq(condition.getUserId()),
                        titleOrCategoryMatch(condition.getKeyword(), trend),
                        categoryEquals(condition.getCategory(), trend)
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }


    // 키워드 검색 조건: 제목, 카테고리명
    private BooleanExpression titleOrCategoryMatch(String keyword, QTrend trend) {
        if (!StringUtils.hasText(keyword)) return null;

        BooleanExpression titleMatch = trend.title.containsIgnoreCase(keyword);

        BooleanExpression categoryMatch = null;
        try {
            TrendCategory matchedCategory = TrendCategory.from(keyword);
            categoryMatch = trend.category.eq(matchedCategory);
        } catch (AppException ignored) {
            return titleMatch; // 카테고리 매칭 실패 시 제목만으로 검색
        }

        return categoryMatch != null ? titleMatch.or(categoryMatch) : titleMatch;
    }


    private BooleanExpression categoryEquals(String category, QTrend trend) {
        if (!StringUtils.hasText(category) || category.equals("all")) return null;
        return trend.category.stringValue().eq(category);
    }

    private OrderSpecifier<?>[] getSortOrder(Pageable pageable, QTrend trend) {
        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            boolean asc = order.isAscending();

            return switch (property) {
                case "createdAt" -> new OrderSpecifier[]{
                        asc ? trend.createdAt.asc() : trend.createdAt.desc()
                };
                case "trendScore" -> new OrderSpecifier[]{
                        asc ? trend.score.asc() : trend.score.desc(),
                        trend.createdAt.desc() // 같은 점수면 최신순
                };
                default -> new OrderSpecifier[]{trend.createdAt.desc()};
            };
        }
        return new OrderSpecifier[]{trend.createdAt.desc()};
    }
}

