package com.chessopeningstats.backend.application.syncGame.internal.ingest;

import com.chessopeningstats.backend.application.syncGame.internal.analysis.dto.AnalyzedGameDto;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.Game;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.AccountNotFoundException;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
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
    private final GamePlayerJdbcRepository gamePlayerJdbcRepository;
    private final GameOpeningJdbcRepository gameOpeningJdbcRepository;
    private final GameJdbcRepository gameJdbcRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final AccountRepository accountRepository;

    @Transactional
    @LogExecutionTime
    public void ingestAll(long playerId, long accountId, Collection<AnalyzedGameDto> dtos, Instant fetchedAt) {
        Player player = playerRepository.findById(playerId).orElseThrow(PlayerNotFoundException::new);
        Account account = accountRepository.findById(accountId).orElseThrow(AccountNotFoundException::new);

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
                                        accountId,
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

        updateAccountLatestPlayedAt(account, dtos);
        updatePlayerLatestSyncedAt(player, fetchedAt);
    }

    @Transactional
    public void updateAccountLatestPlayedAt(Account account, Collection<AnalyzedGameDto> dtos) {
        dtos.stream()
                .map(AnalyzedGameDto::getPlayedAt)
                .max(Instant::compareTo)
                .ifPresent(latest -> {
                    if (account.getLastPlayedAt().isBefore(latest)) {
                        account.setLastPlayedAt(latest);
                    }
                });
    }

    @Transactional
    public void updatePlayerLatestSyncedAt(Player player, Instant syncedAt) {
        if (player.getLastSyncedAt().isBefore(syncedAt))
            player.setLastSyncedAt(syncedAt);
    }
}
