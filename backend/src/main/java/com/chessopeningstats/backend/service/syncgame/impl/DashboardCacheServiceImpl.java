package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.infra.cache.DashboardCache;
import com.chessopeningstats.backend.service.syncgame.DashboardCacheService;
import com.chessopeningstats.backend.service.syncgame.dto.PlayerDashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardCacheServiceImpl implements DashboardCacheService {
    private final DashboardCache dashboardCache;

    @Override
    public void cacheDashboard(PlayerDashboard playerDashboard) {
        dashboardCache.put(playerDashboard.player(), playerDashboard.dashboard());
    }
}
