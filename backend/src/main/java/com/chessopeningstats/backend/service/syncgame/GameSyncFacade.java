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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameSyncFacade {
    private final GameFetchServiceRegistry<RawGame> gameFetchServiceRegistry;
    private final GameNormalizeServiceRegistry<RawGame> gameNormalizeServiceRegistry;
    private final GameSanitizeService gameSanitizeService;
    private final GameAnalyzeService gameAnalyzeService;
    private final DashboardProvideService dashboardProvideService;

    public Mono<Void> syncPlayer(Player player) {
        GameFetchService<RawGame> gameFetchService = gameFetchServiceRegistry.getService(player.platform());
        GameNormalizeService<RawGame> gameNormalizeService = gameNormalizeServiceRegistry.getService(player.platform());

        Flux<RawGame> fetch =
                // 1. game 가져오기
                gameFetchService.fetch(player);

        ParallelFlux<NormalizedGame> normalizedGames =
                // 2. game 정규화
                gameNormalizeService.normalize(fetch, player).parallel().runOn(Schedulers.parallel());

        ParallelFlux<NormalizedGame> sanitizedGames =
                // 3. 특수 룰, 특수 게임 제거
                gameSanitizeService.sanitize(normalizedGames);

        ParallelFlux<AnalyzedGame> analyzedGames =
                // 4. 오프닝 분석
                gameAnalyzeService.analyze(sanitizedGames);

        return
                // 5. 통계 제공
                dashboardProvideService.provideDashboard(analyzedGames, player);
    }
}
