package com.chessopeningstats.backend.service;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.queue.PlayerQueue;
import com.chessopeningstats.backend.infra.queue.PlayerQueueRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerQueueService {
    private final PlayerQueueRegistry playerQueueRegistry;

    public void enqueuePlayer(Player player) {
        PlayerQueue queue = playerQueueRegistry.getQueue(player.platform());
        if (!queue.contains(player))
            queue.enqueue(player);
    }
}