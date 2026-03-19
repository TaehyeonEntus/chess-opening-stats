package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.cache.DashboardCache;
import com.chessopeningstats.backend.service.syncgame.DashboardCacheService;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DashboardCacheServiceImpl implements DashboardCacheService {
    private final DashboardCache dashboardCache;

    @Override
    public Mono<Void> cacheDashboard(Player player, Mono<Dashboard> dashboardMono) {
        return dashboardMono
                .doOnNext(dashboard -> dashboardCache.put(player, dashboard))
                .then();
    }
}
