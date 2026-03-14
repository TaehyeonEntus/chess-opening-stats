package com.chessopeningstats.backend.infra.queue;

import com.chessopeningstats.backend.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class LichessQueue {
    private final BlockingQueue<Player> lichessQueue = new LinkedBlockingQueue<>();

    public void add(Player player) {
        lichessQueue.add(player);
    }

    public Player poll() {
        return lichessQueue.poll();
    }
}
