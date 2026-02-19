package com.chessopeningstats.backend.application.usecase.syncGame.internal.ingest;

import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.application.domain.PlayerService;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.analysis.dto.AnalyzedGameDto;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.domain.Game;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.infra.repository.*;
import com.chessopeningstats.backend.util.LogExecutionTime;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GameIngestService {
    private final GameJdbcRepository gameJdbcRepository;
    private final GamePlayerJdbcRepository gamePlayerJdbcRepository;
    private final GameOpeningJdbcRepository gameOpeningJdbcRepository;

    private final AccountService accountService;
    private final PlayerService playerService;

    @Transactional
    @LogExecutionTime
    public void ingestAll(long accountId, long playerId, Collection<AnalyzedGameDto> dtos, Instant fetchedAt) {
        Account account = accountService.getAccount(accountId);
        Player player = playerService.getPlayer(playerId);

        // Games
        Set<Game> games = dtos.stream()
                .map(AnalyzedGameDto::getGame)
                .collect(Collectors.toSet());
        gameJdbcRepository.upsertGames(games);

        // 종속되는 테이블들이 참조할 ID를 찾기 위해 uuid to id 맵 구성
        Map<String, Long> uuidToIdMap =
                gameJdbcRepository.findIdsByUuid(games.stream()
                        .map(Game::getUuid)
                        .collect(Collectors.toSet()));

        // GamePlayer
        Set<GamePlayerJdbcRepository.GamePlayerRow> gamePlayerRows =
                dtos.stream()
                        .map(dto ->
                                new GamePlayerJdbcRepository.GamePlayerRow(
                                        uuidToIdMap.get(dto.getGame().getUuid()),
                                        playerId,
                                        dto.getGamePlayer().getColor(),
                                        dto.getGamePlayer().getResult()
                                )
                        )
                        .collect(Collectors.toSet());
        gamePlayerJdbcRepository.upsertGamePlayers(gamePlayerRows);

        // GameOpening
        Set<GameOpeningJdbcRepository.GameOpeningRow> gameOpeningRows =
                dtos.stream()
                        .flatMap(dto -> dto.getGameOpenings()
                                .stream()
                                .map(gameOpening ->
                                        new GameOpeningJdbcRepository.GameOpeningRow(
                                                uuidToIdMap.get(dto.getGame().getUuid()),
                                                gameOpening.getOpening().getId()
                                        )
                                )
                        )
                        .collect(Collectors.toSet());
        gameOpeningJdbcRepository.upsertGameOpenings(gameOpeningRows);

        updatePlayerLatestPlayedAt(player, dtos);
        updateAccountLatestSyncedAt(account, fetchedAt);
    }

    @Transactional
    public void updatePlayerLatestPlayedAt(Player player, Collection<AnalyzedGameDto> dtos) {
        dtos.stream()
                .map(AnalyzedGameDto::getPlayedAt)
                .max(Instant::compareTo)
                .ifPresent(latest -> {
                    if (player.getLastPlayedAt().isBefore(latest)) {
                        player.setLastPlayedAt(latest);
                    }
                });
    }

    @Transactional
    public void updateAccountLatestSyncedAt(Account account, Instant syncedAt) {
        if (account.getLastSyncedAt().isBefore(syncedAt))
            account.setLastSyncedAt(syncedAt);
    }
}
