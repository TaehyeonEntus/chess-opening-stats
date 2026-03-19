package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import reactor.core.publisher.Mono;

public interface DashboardCacheService {
    Mono<Void> cacheDashboard(Player player, Mono<Dashboard> dashboard);
}
