package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.cache.DashboardCache;
import com.chessopeningstats.backend.infra.queue.QueueRouter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncGameUseCase {
    private final DashboardCache dashboardCache;
    private final QueueRouter queueRouter;

    public void syncGame(Player player) {
        if (!dashboardCache.contains(player)) queueRouter.add(player);
    }
}
