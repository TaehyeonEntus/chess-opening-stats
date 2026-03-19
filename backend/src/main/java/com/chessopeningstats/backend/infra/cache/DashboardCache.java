package com.chessopeningstats.backend.infra.cache;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DashboardCache {
    private final Cache<Player, Dashboard> cache;

    //파이프라인만 비동기로 일단 구현하자....
    //SSE에 대한 마이그레이션을 준비하자....
    public void put(Player player, Dashboard dashboard) {
        cache.put(player, dashboard);
    }

    public Dashboard get(Player player) {
        return cache.getIfPresent(player);
    }

    public boolean contains(Player player) {
        return cache.asMap().containsKey(player);
    }
}