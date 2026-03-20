package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.service.syncgame.dto.PlayerDashboard;

public interface DashboardCacheService {
    void cacheDashboard(PlayerDashboard playerDashboard);
}
