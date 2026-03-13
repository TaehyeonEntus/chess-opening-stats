package com.chessopeningstats.backend.infra.repository.querydsl;

import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.domain.GamePlayerResult;
import com.chessopeningstats.backend.web.view.dto.home.ColorOpeningStat;
import com.chessopeningstats.backend.web.view.dto.home.GameSummary;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.chessopeningstats.backend.domain.QGame.game;
import static com.chessopeningstats.backend.domain.QGamePlayer.gamePlayer;

@Repository
@RequiredArgsConstructor
public class GameQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<GameSummary> getGameSummary(List<Long> playerIds) {
        return queryFactory
                .select(Projections.constructor(GameSummary.class,
                        gamePlayer.color,
                        gamePlayer.result,
                        game.openingIds
                ))
                .from(game)
                .join(game.gamePlayers, gamePlayer)
                .where(inPlayerIds(playerIds))
                .fetch();
    }

    public List<ColorOpeningStat> getMostPlayedOpeningStats(List<Long> playerIds, GamePlayerColor color, int limit) {
        return queryFactory
                .select(Projections.constructor(ColorOpeningStat.class,
                        gamePlayer.color,
                        game.lastMatchedOpeningId,
                        winCount(),
                        drawCount(),
                        loseCount()
                ))
                .from(game)
                .join(game.gamePlayers, gamePlayer)
                .where(inPlayerIds(playerIds), eqColor(color))
                .groupBy(gamePlayer.color, game.lastMatchedOpeningId)
                .orderBy(orderByMostPlayed())
                .limit(limit)
                .fetch();
    }

    public List<ColorOpeningStat> getHighestWinRateOpeningStats(List<Long> playerIds, GamePlayerColor color, int limit) {
        return queryFactory
                .select(Projections.constructor(ColorOpeningStat.class,
                        gamePlayer.color,
                        game.lastMatchedOpeningId,
                        winCount(),
                        drawCount(),
                        loseCount()
                ))
                .from(game)
                .join(game.gamePlayers, gamePlayer)
                .where(inPlayerIds(playerIds), eqColor(color))
                .groupBy(gamePlayer.color, game.lastMatchedOpeningId)
                .having(game.lastMatchedOpeningId.count().goe(10))
                .orderBy(orderByWinRate())
                .limit(limit)
                .fetch();
    }

    // === Where ===
    private BooleanExpression inPlayerIds(List<Long> playerIds) {
        return gamePlayer.player.id.in(playerIds);
    }

    private BooleanExpression eqColor(GamePlayerColor color) {
        return gamePlayer.color.eq(color);
    }

    // === OrderBy ===
    private OrderSpecifier<Long> orderByMostPlayed() {
        return gamePlayer.count().desc();
    }

    private OrderSpecifier<Double> orderByWinRate() {
        return winRate().desc();
    }

    private NumberExpression<Double> winRate() {
        return new CaseBuilder()
                .when(gamePlayer.result.eq(GamePlayerResult.WIN)).then(1.0)
                .otherwise(0.0).sum()
                .divide(gamePlayer.count());
    }

    private NumberExpression<Long> winCount() {
        return new CaseBuilder()
                .when(gamePlayer.result.eq(GamePlayerResult.WIN)).then(1L)
                .otherwise(0L).sum();
    }

    private NumberExpression<Long> drawCount() {
        return new CaseBuilder()
                .when(gamePlayer.result.eq(GamePlayerResult.DRAW)).then(1L)
                .otherwise(0L).sum();
    }

    private NumberExpression<Long> loseCount() {
        return new CaseBuilder()
                .when(gamePlayer.result.eq(GamePlayerResult.LOSE)).then(1L)
                .otherwise(0L).sum();
    }
}