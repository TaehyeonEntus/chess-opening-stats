package com.chessopeningstats.backend.infra.queue;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.PlayerEnqueueException;
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
    public void enqueue(Player player) {
        try {
            lichessQueue.put(player);
        } catch (InterruptedException e) {
            throw new PlayerEnqueueException(e);
        }
    }

    @Override
    public Player dequeue() {
        try {
            return lichessQueue.take();
        } catch (InterruptedException e) {
            throw new PlayerEnqueueException(e);
        }
    }
}
