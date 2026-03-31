package com.chessopeningstats.backend.service.playerdashboard.impl;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.DashboardNotFoundException;
import com.chessopeningstats.backend.infra.cache.DashboardCache;
import com.chessopeningstats.backend.service.playerdashboard.dto.Dashboard;
import com.chessopeningstats.backend.service.playerdashboard.dto.PlayerDashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardCacheService {
    private final DashboardCache dashboardCache;

    public void cacheDashboard(PlayerDashboard playerDashboard) {
        dashboardCache.put(playerDashboard.player(), playerDashboard.dashboard());
    }

    public Dashboard get(Player player) {
        Dashboard dashboard = dashboardCache.get(player);
        if (dashboard == null)
            throw new DashboardNotFoundException();
        return dashboard;
    }
}
