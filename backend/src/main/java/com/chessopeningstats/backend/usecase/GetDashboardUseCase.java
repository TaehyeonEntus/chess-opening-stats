package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.cache.DashboardCache;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetDashboardUseCase {
    private final DashboardCache dashboardCache;

    public Dashboard getDashboard(Player player) {
        return dashboardCache.get(player);
    }
}
