package com.chessopeningstats.backend.service;

import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.infra.repository.GameRepository;
import com.chessopeningstats.backend.infra.repository.batch.GameBatchRepository;
import com.chessopeningstats.backend.infra.repository.batch.dto.GameRow;
import com.chessopeningstats.backend.infra.repository.querydsl.GameQueryRepository;
import com.chessopeningstats.backend.web.view.dto.home.ColorOpeningStat;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final GameQueryRepository gameQueryRepository;
    private final GameBatchRepository gameBatchRepository;

    @Transactional
    public void upsertBatch(List<GameRow> gameRows) {
        gameBatchRepository.upsertBatch(gameRows);
    }

    @Transactional
    public void garbageCollect() {
        List<Long> orphanGameIds = gameRepository.findOrphanGameIds();
        if (!orphanGameIds.isEmpty())
            gameBatchRepository.deleteBatch(orphanGameIds);
    }

    public Map<GamePlayerColor, List<ColorOpeningStat>> getOpeningStats(List<Long> playerIds) {
        Map<GamePlayerColor, Map<Long, long[]>> statsMap = new HashMap<>();

        gameQueryRepository.getGameSummary(playerIds)
                .forEach(gameSummary ->
                        gameSummary.ids().forEach(openingId -> {
                            long[] stats = statsMap
                                    .computeIfAbsent(gameSummary.color(), k -> new HashMap<>())
                                    .computeIfAbsent(openingId, k -> new long[3]);

                            switch (gameSummary.result()) {
                                case WIN -> stats[0]++;
                                case DRAW -> stats[1]++;
                                case LOSE -> stats[2]++;
                            }
                        })
                );

        return statsMap.entrySet().stream()
                .flatMap(e -> e.getValue().entrySet().stream()
                        .map(entry -> new ColorOpeningStat(
                                e.getKey(),
                                entry.getKey(),
                                entry.getValue()[0],
                                entry.getValue()[1],
                                entry.getValue()[2]
                        )))
                .collect(Collectors.groupingBy(ColorOpeningStat::color));
    }

    public List<ColorOpeningStat> getMostPlayedOpeningStats(List<Long> playerIds, GamePlayerColor color) {
        return gameQueryRepository.getMostPlayedOpeningStats(playerIds, color, 5);
    }

    public List<ColorOpeningStat> getHighestWinRateOpeningStats(List<Long> playerIds, GamePlayerColor color) {
        return gameQueryRepository.getHighestWinRateOpeningStats(playerIds, color, 5);
    }
}
