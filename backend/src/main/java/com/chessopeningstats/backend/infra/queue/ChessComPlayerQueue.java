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
public class ChessComPlayerQueue implements PlayerQueue {
    private final BlockingQueue<Player> chessComQueue = new LinkedBlockingQueue<>();

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public int size() {
        return chessComQueue.size();
    }

    @Override
    public boolean contains(Player player) {
        return chessComQueue.contains(player);
    }

    @Override
    public void enqueue(Player player){
        try {
            chessComQueue.put(player);
        } catch (InterruptedException e) {
            throw new PlayerEnqueueException(e);
        }
    }

    @Override
    public Player dequeue(){
        try {
            return chessComQueue.take();
        } catch (InterruptedException e) {
            throw new PlayerEnqueueException(e);
        }
    }
}
