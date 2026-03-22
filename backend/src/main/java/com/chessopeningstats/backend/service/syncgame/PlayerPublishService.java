package com.chessopeningstats.backend.service.syncgame;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import reactor.core.publisher.Flux;

import java.util.List;

public interface PlayerPublishService {
    Platform platform();

    Flux<Player> publishPlayer();
}
