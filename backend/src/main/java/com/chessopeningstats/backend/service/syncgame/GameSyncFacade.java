package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import com.chessopeningstats.backend.service.syncgame.registry.GameFetchServiceRegistry;
import com.chessopeningstats.backend.service.syncgame.registry.GameNormalizeServiceRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSyncFacade {
    private final GameFetchServiceRegistry<RawGame> gameFetchServiceRegistry;
    private final GameNormalizeServiceRegistry<RawGame> gameNormalizeServiceRegistry;
    private final GameSanitizeService gameSanitizeService;
    private final GameAnalyzeService gameAnalyzeService;
    private final DashboardProvideService dashboardProvideService;

    //테스트용
    public Mono<Void> syncPlayer(Player player) {
        GameFetchService<RawGame> gameFetchService = gameFetchServiceRegistry.getService(player.platform());
        GameNormalizeService<RawGame> gameNormalizeService = gameNormalizeServiceRegistry.getService(player.platform());

        ParallelFlux<RawGame> rawGames = gameFetchService.fetch(player);
        ParallelFlux<NormalizedGame> normalizedGames = gameNormalizeService.normalize(rawGames, player);
        ParallelFlux<NormalizedGame> sanitizedGames = gameSanitizeService.sanitize(normalizedGames);
        ParallelFlux<AnalyzedGame> analyzedGames = gameAnalyzeService.analyze(sanitizedGames);
        return dashboardProvideService.provideDashboard(analyzedGames, player);
    }
}
