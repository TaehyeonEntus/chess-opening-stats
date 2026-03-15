package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.domain.Color;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.Result;
import com.chessopeningstats.backend.infra.cache.DashboardCache;
import com.chessopeningstats.backend.service.syncgame.DashboardProvideService;
import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.syncgame.dto.ColorDashboard;
import com.chessopeningstats.backend.service.syncgame.dto.ColorOpeningStat;
import com.chessopeningstats.backend.service.syncgame.dto.ColorRecord;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardProvideServiceImpl implements DashboardProvideService {
    private final DashboardCache dashboardCache;

    @Override
    public Mono<Void> provideDashboard(ParallelFlux<AnalyzedGame> analyzedGames, Player player) {
        return analyzedGames
                .reduce(DashboardAccumulator::new, DashboardAccumulator::accumulate)
                .reduce(DashboardAccumulator::combine)
                .map(DashboardAccumulator::toDashboard)
                .doOnNext(dashboard -> dashboardCache.cache(player, dashboard))
                .then();
    }

    private static class DashboardAccumulator {
        private final Map<Color, ColorStats> statsMap = new EnumMap<>(Color.class);

        public DashboardAccumulator() {
            statsMap.put(Color.WHITE, new ColorStats(Color.WHITE));
            statsMap.put(Color.BLACK, new ColorStats(Color.BLACK));
        }

        public DashboardAccumulator accumulate(AnalyzedGame game) {
            ColorStats colorStats = statsMap.get(game.color());
            if (colorStats != null) {
                colorStats.addGame(game);
            }
            return this;
        }

        public DashboardAccumulator combine(DashboardAccumulator other) {
            this.statsMap.get(Color.WHITE).combine(other.statsMap.get(Color.WHITE));
            this.statsMap.get(Color.BLACK).combine(other.statsMap.get(Color.BLACK));
            return this;
        }

        public Dashboard toDashboard() {
            return new Dashboard(
                    statsMap.get(Color.WHITE).toColorDashboard(),
                    statsMap.get(Color.BLACK).toColorDashboard()
            );
        }
    }

    private static class ColorStats {
        private final Color color;
        private long win = 0;
        private long draw = 0;
        private long lose = 0;
        private final Map<Long, OpeningStatBuilder> lastOpeningMap = new HashMap<>();
        private final Map<Long, OpeningStatBuilder> allOpeningsMap = new HashMap<>();

        public ColorStats(Color color) {
            this.color = color;
        }

        public void addGame(AnalyzedGame game) {
            Result result = game.result();
            if (result == Result.WIN) win++;
            else if (result == Result.DRAW) draw++;
            else if (result == Result.LOSE) lose++;

            if (game.lastOpeningId() != null) {
                lastOpeningMap.computeIfAbsent(game.lastOpeningId(), id -> new OpeningStatBuilder(id, color))
                        .addResult(result);
            }

            if (game.openingIds() != null) {
                for (Long openingId : game.openingIds()) {
                    allOpeningsMap.computeIfAbsent(openingId, id -> new OpeningStatBuilder(id, color))
                            .addResult(result);
                }
            }
        }

        public void combine(ColorStats other) {
            this.win += other.win;
            this.draw += other.draw;
            this.lose += other.lose;
            other.lastOpeningMap.forEach((id, builder) -> 
                this.lastOpeningMap.computeIfAbsent(id, i -> new OpeningStatBuilder(i, color)).combine(builder));
            other.allOpeningsMap.forEach((id, builder) -> 
                this.allOpeningsMap.computeIfAbsent(id, i -> new OpeningStatBuilder(i, color)).combine(builder));
        }

        public ColorDashboard toColorDashboard() {
            ColorRecord record = new ColorRecord(color, win, draw, lose);

            // 1. Most Played (Variation 기준)
            List<ColorOpeningStat> mostPlayed = lastOpeningMap.values().stream()
                    .map(OpeningStatBuilder::toStat)
                    .sorted((a, b) -> Long.compare(getTotal(b), getTotal(a)))
                    .limit(10)
                    .toList();

            // 2. Highest Win Rate (최소 10판 이상 플레이한 최종 단계 오프닝 기준)
            List<ColorOpeningStat> highestWinRate = lastOpeningMap.values().stream()
                    .filter(builder -> builder.getTotalGames() >= 10)
                    .map(OpeningStatBuilder::toStat)
                    .sorted((a, b) -> {
                        int winRateCompare = Double.compare(getWinRate(b), getWinRate(a));
                        if (winRateCompare != 0) return winRateCompare;
                        return Long.compare(getTotal(b), getTotal(a)); // 승률 같으면 판수 많은 순
                    })
                    .limit(10)
                    .toList();

            // 3. All Openings (플레이한 모든 단계 오프닝 목록)
            List<ColorOpeningStat> allOpenings = allOpeningsMap.values().stream()
                    .map(OpeningStatBuilder::toStat)
                    .sorted((a, b) -> Long.compare(getTotal(b), getTotal(a)))
                    .toList();

            return new ColorDashboard(record, mostPlayed, highestWinRate, allOpenings);
        }

        private long getTotal(ColorOpeningStat stat) {
            return stat.win() + stat.draw() + stat.lose();
        }

        private double getWinRate(ColorOpeningStat stat) {
            long total = getTotal(stat);
            return total == 0 ? 0 : (double) stat.win() / total;
        }
    }

    private static class OpeningStatBuilder {
        private final Long openingId;
        private final Color color;
        private long win = 0;
        private long draw = 0;
        private long lose = 0;

        public OpeningStatBuilder(Long openingId, Color color) {
            this.openingId = openingId;
            this.color = color;
        }

        public void addResult(Result result) {
            if (result == Result.WIN) win++;
            else if (result == Result.DRAW) draw++;
            else if (result == Result.LOSE) lose++;
        }

        public void combine(OpeningStatBuilder other) {
            this.win += other.win;
            this.draw += other.draw;
            this.lose += other.lose;
        }

        public long getTotalGames() {
            return win + draw + lose;
        }

        public ColorOpeningStat toStat() {
            return new ColorOpeningStat(openingId, color, win, draw, lose);
        }
    }
}
