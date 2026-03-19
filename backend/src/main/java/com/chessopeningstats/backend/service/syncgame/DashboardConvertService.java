package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.syncgame.dto.AnalyzedGame;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;

public interface DashboardConvertService {
    Mono<Dashboard> convertDashboard(ParallelFlux<AnalyzedGame> analyzedGames, Player player);
}
