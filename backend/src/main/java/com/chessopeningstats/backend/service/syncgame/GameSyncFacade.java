package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.syncgame.registry.GameFetchServiceRegistry;
import com.chessopeningstats.backend.service.syncgame.registry.GameNormalizeServiceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSyncFacade {
    private final GameFetchServiceRegistry<RawGame> gameFetchServiceRegistry;
    private final GameNormalizeServiceRegistry<RawGame> gameNormalizeServiceRegistry;
    private final GameSanitizeService gameSanitizeService;
    private final GameAnalyzeService gameAnalyzeService;
    private final DashboardConvertService dashboardConvertService;
    private final DashboardCacheService dashboardCacheService;

    public Mono<Void> syncGames(Player player) {
        GameFetchService<RawGame> gameFetchService = gameFetchServiceRegistry.getService(player.platform());
        GameNormalizeService<RawGame> gameNormalizeService = gameNormalizeServiceRegistry.getService(player.platform());

        return gameFetchService.fetch(player)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(gameNormalizeService::normalize)
                .filter(gameSanitizeService::sanitize)
                .map(gameAnalyzeService::analyze)
                .sequential()
                .groupBy(AnalyzedGame::player)
                .flatMap(Flux::collectList)
                .map(dashboardConvertService::convertDashboard)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(dashboardCacheService::cacheDashboard)
                .then();
    }
}
