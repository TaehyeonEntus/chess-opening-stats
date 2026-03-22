package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.syncgame.registry.GameFetchServiceRegistry;
import com.chessopeningstats.backend.service.syncgame.registry.GameNormalizeServiceRegistry;
import com.chessopeningstats.backend.service.syncgame.registry.PlayerPublishServiceRegistry;
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
    private final PlayerPublishServiceRegistry playerPublishServiceRegistry;
    private final GameFetchServiceRegistry<RawGame> gameFetchServiceRegistry;
    private final GameNormalizeServiceRegistry<RawGame> gameNormalizeServiceRegistry;
    private final GameSanitizeService gameSanitizeService;
    private final GameAnalyzeService gameAnalyzeService;
    private final DashboardConvertService dashboardConvertService;
    private final DashboardCacheService dashboardCacheService;

    public Mono<Void> syncGames(Platform platform) {
        PlayerPublishService playerPublishService = playerPublishServiceRegistry.getService(platform);
        GameFetchService<RawGame> gameFetchService = gameFetchServiceRegistry.getService(platform);
        GameNormalizeService<RawGame> gameNormalizeService = gameNormalizeServiceRegistry.getService(platform);

        return playerPublishService.publishPlayer()
                //fetch 직렬화 안하면 429 error 터집니다
                .concatMap(gameFetchService::fetch)
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
