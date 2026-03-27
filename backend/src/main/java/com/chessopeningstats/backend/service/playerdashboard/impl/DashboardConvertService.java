package com.chessopeningstats.backend.service.playerdashboard.impl;

import com.chessopeningstats.backend.domain.Color;
import com.chessopeningstats.backend.domain.Result;
import com.chessopeningstats.backend.service.playerdashboard.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 색상 별로 분리하고 계산
 */
@Service
@RequiredArgsConstructor
public class DashboardConvertService {
    public PlayerDashboard convertDashboard(List<AnalyzedGame> groupedGames) {
        List<AnalyzedGame> white = new ArrayList<>();
        List<AnalyzedGame> black = new ArrayList<>();

        groupedGames.forEach(game -> {
            if (game.color() == Color.WHITE) white.add(game);
            if (game.color() == Color.BLACK) black.add(game);
        });

        return new PlayerDashboard(
                groupedGames.getFirst().player(),
                new Dashboard(
                        buildColorDashboard(white),
                        buildColorDashboard(black)
                )
        );
    }

    private ColorDashboard buildColorDashboard(List<AnalyzedGame> games) {
        Map<Long, OpeningRecord> lastOpeningMap = new HashMap<>();
        Map<Long, OpeningRecord> allOpeningsMap = new HashMap<>();

        long win = 0;
        long draw = 0;
        long lose = 0;

        for (AnalyzedGame game : games) {
            if (game.result() == Result.WIN) win++;
            if (game.result() == Result.DRAW) draw++;
            if (game.result() == Result.LOSE) lose++;
        }

        games.forEach(game -> {
            lastOpeningMap
                    .computeIfAbsent(game.lastOpeningId(), OpeningRecord::new)
                    .addResult(game.result());

            game.openingIds().forEach(id -> allOpeningsMap
                    .computeIfAbsent(id, OpeningRecord::new)
                    .addResult(game.result())
            );
        });

        List<OpeningStat> mostPlayedOpeningStats = lastOpeningMap.values().stream()
                .sorted(Comparator.comparingLong(OpeningRecord::total).reversed())
                .limit(5)
                .map(OpeningRecord::toOpeningStat)
                .toList();

        List<OpeningStat> highestWinrateOpeningStats = allOpeningsMap.values().stream()
                .sorted(Comparator.comparingDouble(OpeningRecord::winRate).thenComparing(OpeningRecord::total).reversed())
                .filter(openingRecord -> openingRecord.total() >= 10)
                .limit(5)
                .map(OpeningRecord::toOpeningStat)
                .toList();

        List<OpeningStat> openingStats = allOpeningsMap.values().stream()
                .sorted(Comparator.comparingLong(OpeningRecord::total).reversed())
                .map(OpeningRecord::toOpeningStat)
                .toList();

        return new ColorDashboard(
                new Stat(win, draw, lose),
                mostPlayedOpeningStats,
                highestWinrateOpeningStats,
                openingStats
        );
    }

    @RequiredArgsConstructor
    private static class OpeningRecord {
        private final Long openingId;

        private long win = 0;
        private long draw = 0;
        private long lose = 0;

        public void addResult(Result result) {
            switch (result) {
                case WIN -> win++;
                case DRAW -> draw++;
                case LOSE -> lose++;
            }
        }

        public long total() {
            return win + draw + lose;
        }

        public double winRate() {
            return total() == 0 ? 0 : (double) win / total();
        }

        public OpeningStat toOpeningStat() {
            return new OpeningStat(openingId, new Stat(win, draw, lose));
        }
    }
}
