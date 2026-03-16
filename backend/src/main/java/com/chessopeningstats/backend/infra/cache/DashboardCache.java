package com.chessopeningstats.backend.infra.cache;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class DashboardCache {
    private final Cache<Player, Dashboard> dashboardCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(100)
            .scheduler(Scheduler.systemScheduler())
            .build();

    public void cache(Player player, Dashboard dashboard) {
        dashboardCache.put(player, dashboard);
    }

    public Dashboard get(Player player) {
        return dashboardCache.getIfPresent(player);
    }

    public boolean contains(Player player) {
        return dashboardCache.getIfPresent(player) != null;
    }
}
