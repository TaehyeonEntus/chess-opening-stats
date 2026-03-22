package com.chessopeningstats.backend.infra.queue.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.queue.PlayerQueue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Component
@RequiredArgsConstructor
public class LichessPlayerQueue implements PlayerQueue {
    private final BlockingQueue<Player> lichessQueue = new LinkedBlockingQueue<>();

    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    public int size() {
        return lichessQueue.size();
    }

    @Override
    public boolean contains(Player player) {
        return lichessQueue.contains(player);
    }

    @Override
    public void enqueue(Player player) throws InterruptedException {
        lichessQueue.put(player);
    }

    @Override
    public Player dequeue() throws InterruptedException {
        return lichessQueue.take();
    }
}
