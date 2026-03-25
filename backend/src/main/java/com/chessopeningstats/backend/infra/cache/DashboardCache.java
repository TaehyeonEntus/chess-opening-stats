package com.chessopeningstats.backend.infra.cache;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardCache {
    private final Cache<Player, Dashboard> cache;

    public void put(Player player, Dashboard dashboard) {
        cache.put(player, dashboard);
    }

    public Dashboard get(Player player) {
        return cache.getIfPresent(player);
    }
}