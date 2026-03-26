package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.PlayerAlreadyInQueueException;
import com.chessopeningstats.backend.infra.queue.PlayerQueue;
import com.chessopeningstats.backend.infra.queue.registry.PlayerQueueRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SyncPlayerUseCase {
    private final PlayerQueueRegistry playerQueueRegistry;

    public void syncPlayer(Player player) throws InterruptedException {
        PlayerQueue queue = playerQueueRegistry.getQueue(player.platform());

        if (queue.contains(player))
            throw new PlayerAlreadyInQueueException();
        else
            queue.enqueue(player);
    }
}
