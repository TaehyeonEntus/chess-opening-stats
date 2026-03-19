package com.chessopeningstats.backend.infra.cache;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DashboardCache {
    private final Cache<Player, Dashboard> caffeine;

    //파이프라인만 비동기로 일단 구현하자....
    public void put(Player player, Dashboard dashboard) {
        caffeine.put(player, dashboard);
    }

    //동기
    public Dashboard get(Player player) {
        return caffeine.getIfPresent(player);
    }

    //동기
    public boolean contains(Player player) {
        return caffeine.asMap().containsKey(player);
    }
}