package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import com.chessopeningstats.backend.service.AccountPlayerService;
import com.chessopeningstats.backend.service.AccountService;
import com.chessopeningstats.backend.service.GamePlayerService;
import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import com.chessopeningstats.backend.service.syncgame.registry.GameFetchServiceRegistry;
import com.chessopeningstats.backend.service.syncgame.registry.GameNormalizeServiceRegistry;
import com.chessopeningstats.backend.util.logger.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSyncFacade {
    private final GameSyncQueue gameSyncQueue;
    private final ExecutorService virtualThreadPool;

    private final AccountService accountService;
    private final GamePlayerService gamePlayerService;
    private final AccountPlayerService accountPlayerService;

    private final GameFetchServiceRegistry<RawGame> gameFetchServiceRegistry;
    private final GameNormalizeServiceRegistry<RawGame> gameNormalizeServiceRegistry;
    private final GameSanitizeService gameSanitizeService;
    private final GameAnalyzeService gameAnalyzeService;
    private final GameIngestService gameIngestService;

    public void syncAccount(long accountId) {
        accountPlayerService.getPlayersByAccountId(accountId).forEach(gameSyncQueue::add);
        accountService.updateLastSyncedAt(accountId, Instant.now());
    }

    //운영용
    @LogExecutionTime
    public Mono<Void> syncPlayer(Player player) {
        GameFetchService<RawGame> gameFetchService = gameFetchServiceRegistry.getService(player.getPlatform());
        GameNormalizeService<RawGame> gameNormalizeService = gameNormalizeServiceRegistry.getService(player.getPlatform());

        Flux<RawGame> fetch =
                gameFetchService.fetch(player);

        ParallelFlux<NormalizedGame> normalizedGames =
                gameNormalizeService.normalize(fetch, player.getUsername()).parallel().runOn(Schedulers.parallel());

        ParallelFlux<NormalizedGame> sanitizedGames =
                gameSanitizeService.sanitize(normalizedGames);

        ParallelFlux<AnalyzedGame> analyzedGames =
                gameAnalyzeService.analyze(sanitizedGames, player.getId());

        return gameIngestService.ingest(analyzedGames)
                .then(Mono.fromRunnable(() -> gamePlayerService.updateLastPlayedAt(player.getId()))
                        .subscribeOn(Schedulers.fromExecutor(virtualThreadPool)).then());
    }
}
