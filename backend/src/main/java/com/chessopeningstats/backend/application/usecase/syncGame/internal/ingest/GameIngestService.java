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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
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
        if(!games.isEmpty())
            gameJdbcRepository.upsertGames(games);

        // GamePlayer
        Set<GamePlayerJdbcRepository.GamePlayerRow> gamePlayerRows =
                dtos.stream()
                        .map(dto ->
                                new GamePlayerJdbcRepository.GamePlayerRow(
                                        dto.getGame().getId(),
                                        playerId,
                                        dto.getGamePlayer().getColor(),
                                        dto.getGamePlayer().getResult()
                                )
                        )
                        .collect(Collectors.toSet());
        if(!gamePlayerRows.isEmpty())
            gamePlayerJdbcRepository.upsertGamePlayers(gamePlayerRows);

        // GameOpening
        Set<GameOpeningJdbcRepository.GameOpeningRow> gameOpeningRows =
                dtos.stream()
                        .flatMap(dto -> dto.getGameOpenings()
                                .stream()
                                .map(gameOpening ->
                                        new GameOpeningJdbcRepository.GameOpeningRow(
                                                gameOpening.getGame().getId(),
                                                gameOpening.getOpening().getId()
                                        )
                                )
                        )
                        .collect(Collectors.toSet());
        if(!gameOpeningRows.isEmpty())
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
        accountService.updateLastSyncedAt(account.getId(), syncedAt);
    }
}
