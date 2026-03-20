package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.PlayerAlreadyInQueueException;
import com.chessopeningstats.backend.infra.queue.PlayerQueue;
import com.chessopeningstats.backend.infra.queue.registry.PlayerQueueRegistry;
import com.chessopeningstats.backend.web.dto.EnqueuePlayerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnqueuePlayerUseCase {
    private final PlayerQueueRegistry playerQueueRegistry;

    public EnqueuePlayerResponse enqueuePlayer(Player player) {
        PlayerQueue queue = playerQueueRegistry.getQueue(player.platform());

        if (queue.contains(player))
            throw new PlayerAlreadyInQueueException();

        int sizeBeforeEnqueue = queue.size();
        queue.enqueue(player);

        return new EnqueuePlayerResponse(sizeBeforeEnqueue);
    }
}
