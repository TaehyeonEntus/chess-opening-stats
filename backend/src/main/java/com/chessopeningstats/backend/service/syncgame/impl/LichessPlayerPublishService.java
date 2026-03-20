package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.queue.impl.LichessPlayerQueue;
import com.chessopeningstats.backend.service.syncgame.PlayerPublishService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LichessPlayerPublishService implements PlayerPublishService {
    private final LichessPlayerQueue lichessPlayerQueue;

    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    public List<Player> publishPlayer() {
        return lichessPlayerQueue.dequeueAll();
    }
}
