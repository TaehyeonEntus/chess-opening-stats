package com.chessopeningstats.backend.service.playerdashboard;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import com.chessopeningstats.backend.service.playerdashboard.impl.DashboardCacheService;
import com.chessopeningstats.backend.service.playerdashboard.impl.DashboardConvertService;
import com.chessopeningstats.backend.service.playerdashboard.impl.GameAnalyzeService;
import com.chessopeningstats.backend.service.playerdashboard.impl.GameSanitizeService;
import com.chessopeningstats.backend.service.playerdashboard.registry.GameFetchServiceRegistry;
import com.chessopeningstats.backend.service.playerdashboard.registry.GameNormalizeServiceRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
public class PlayerDashboardFacade {
    private final GameFetchServiceRegistry<RawGame> gameFetchServiceRegistry;
    private final GameNormalizeServiceRegistry<RawGame> gameNormalizeServiceRegistry;
    private final GameSanitizeService gameSanitizeService;
    private final GameAnalyzeService gameAnalyzeService;
    private final DashboardConvertService dashboardConvertService;
    private final DashboardCacheService dashboardCacheService;

    public Mono<Void> getPlayerDashboard(Player player) {
        GameFetchService<RawGame> gameFetchService = gameFetchServiceRegistry.getService(player.platform());
        GameNormalizeService<RawGame> gameNormalizeService = gameNormalizeServiceRegistry.getService(player.platform());
        //네트워크 바운드
        return gameFetchService.fetch(player)

                //CPU 바운드 (병렬 전환)
                .parallel()
                .runOn(Schedulers.parallel())
                .map(gameNormalizeService::normalize)
                .filter(gameSanitizeService::sanitize)
                .map(gameAnalyzeService::analyze)

                //기록 합산을 위해 직렬화
                .sequential()
                .collectList()
                .map(dashboardConvertService::convertDashboard)

                //I/O 바운드 (현재는 캐시만 DB X)
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(dashboardCacheService::cacheDashboard)
                .then();
    }
}
