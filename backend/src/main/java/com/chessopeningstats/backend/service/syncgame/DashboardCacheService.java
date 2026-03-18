package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;

public interface DashboardCacheService {
    Mono<Void> cacheDashboard(ParallelFlux<AnalyzedGame> analyzedGames, Player player);
}
