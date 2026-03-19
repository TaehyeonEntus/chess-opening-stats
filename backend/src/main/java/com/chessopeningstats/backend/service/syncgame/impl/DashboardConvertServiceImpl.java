package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.domain.Color;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.Result;
import com.chessopeningstats.backend.service.syncgame.DashboardConvertService;
import com.chessopeningstats.backend.service.syncgame.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;

import java.util.*;
import java.util.function.Predicate;

/**
 * 1 . 레일 별 분할
 * 2 . 색깔 별 분할
 * 3 . 오프닝 별 분할
 * 4 . 결과 기록 후 역순으로 병합
 */
@Service
@RequiredArgsConstructor
public class DashboardConvertServiceImpl implements DashboardConvertService {
    @Override
    public Mono<Dashboard> convertDashboard(ParallelFlux<AnalyzedGame> analyzedGames, Player player) {
        return analyzedGames
                //레일별로 스토리지에 저장
                .reduce(StatStorage::new, StatStorage::store)
                //스토리지 병합
                .reduce(StatStorage::combine)
                //대시보드로 변환
                .map(StatStorage::toDashboard);
    }

    private static class StatStorage {
        private final Map<Color, ColorStat> colorStatMap = new EnumMap<>(Map.of(
                Color.WHITE, new ColorStat(),
                Color.BLACK, new ColorStat()
        ));

        public StatStorage store(AnalyzedGame game) {
            colorStatMap.get(game.color()).addGame(game);
            return this;
        }

        public StatStorage combine(StatStorage other) {
            colorStatMap.forEach((color, stat) -> stat.combine(other.colorStatMap.get(color)));
            return this;
        }

        public Dashboard toDashboard() {
            return new Dashboard(
                    colorStatMap.get(Color.WHITE).toColorDashboard(),
                    colorStatMap.get(Color.BLACK).toColorDashboard()
            );
        }
    }

    private static class ColorStat {
        private final Map<Long, OpeningRecord> lastOpeningMap = new HashMap<>();
        private final Map<Long, OpeningRecord> allOpeningsMap = new HashMap<>();

        private long win = 0;
        private long draw = 0;
        private long lose = 0;

        public void addGame(AnalyzedGame game) {
            Result result = game.result();

            switch (result) {
                case WIN -> win++;
                case DRAW -> draw++;
                case LOSE -> lose++;
            }

            lastOpeningMap.computeIfAbsent(game.lastOpeningId(), OpeningRecord::new).addResult(result);
            game.openingIds().forEach(id -> allOpeningsMap.computeIfAbsent(id, OpeningRecord::new).addResult(result));
        }

        public void combine(ColorStat other) {
            win += other.win;
            draw += other.draw;
            lose += other.lose;
            other.lastOpeningMap.forEach((id, s) -> lastOpeningMap.computeIfAbsent(id, OpeningRecord::new).combine(s));
            other.allOpeningsMap.forEach((id, s) -> allOpeningsMap.computeIfAbsent(id, OpeningRecord::new).combine(s));
        }

        public ColorDashboard toColorDashboard() {
            Comparator<OpeningStat> byTotal = Comparator.<OpeningStat, Long>comparing(s -> s.stat().total()).reversed();
            Comparator<OpeningStat> byWinRate = Comparator.<OpeningStat, Double>comparing(s -> s.stat().winRate()).reversed().thenComparing(byTotal);

            return new ColorDashboard(
                    // 승, 무, 패
                    new Stat(win, draw, lose),
                    // 최다 플레이 오프닝
                    rankOpenings(lastOpeningMap, byTotal, 5, s -> true),
                    // 최고 승률 오프닝
                    rankOpenings(allOpeningsMap, byWinRate, 5, s -> s.total() >= 10),
                    // 전체 오프닝
                    rankOpenings(allOpeningsMap, byTotal, Integer.MAX_VALUE, s -> true)
            );
        }

        private List<OpeningStat> rankOpenings(Map<Long, OpeningRecord> map, Comparator<OpeningStat> comparator, int limit, Predicate<OpeningRecord> filter) {
            return map.values().stream()
                    .filter(filter)
                    .map(OpeningRecord::toOpeningStat)
                    .sorted(comparator)
                    .limit(limit)
                    .toList();
        }
    }

    @RequiredArgsConstructor
    private static class OpeningRecord {
        private final Long openingId;
        private long win = 0, draw = 0, lose = 0;

        public void addResult(Result result) {
            switch (result) {
                case WIN -> win++;
                case DRAW -> draw++;
                case LOSE -> lose++;
            }
        }

        public void combine(OpeningRecord other) {
            win += other.win;
            draw += other.draw;
            lose += other.lose;
        }

        public long total() {
            return win + draw + lose;
        }

        public OpeningStat toOpeningStat() {
            return new OpeningStat(openingId, new Stat(win, draw, lose));
        }
    }
}
