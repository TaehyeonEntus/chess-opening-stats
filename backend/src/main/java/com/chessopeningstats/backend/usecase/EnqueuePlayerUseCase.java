package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.cache.DashboardCache;
import com.chessopeningstats.backend.infra.queue.SyncQueue;
import com.chessopeningstats.backend.web.dto.SyncGameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnqueuePlayerUseCase {
    private final DashboardCache dashboardCache;
    private final SyncQueue syncQueue;

    public SyncGameResponse syncGame(Player player) {
        int size = syncQueue.size(player.platform());

        if (!isCached(player))
            syncQueue.add(player);

        return new SyncGameResponse(size);
    }

    private boolean isCached(Player player) {
        return dashboardCache.contains(player) || syncQueue.contains(player);
    }
}
