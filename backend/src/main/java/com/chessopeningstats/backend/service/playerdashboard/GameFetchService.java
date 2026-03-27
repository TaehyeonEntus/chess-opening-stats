package com.chessopeningstats.backend.service.playerdashboard;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import reactor.core.publisher.Flux;

public interface GameFetchService<T extends RawGame> {
    Platform platform();

    Flux<T> fetch(Player player);
}
