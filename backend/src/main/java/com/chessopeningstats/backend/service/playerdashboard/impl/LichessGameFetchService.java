package com.chessopeningstats.backend.service.playerdashboard.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playergames.dto.LichessRawGame;
import com.chessopeningstats.backend.infra.client.playergames.LichessPlayerGamesClient;
import com.chessopeningstats.backend.service.playerdashboard.GameFetchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class LichessGameFetchService implements GameFetchService<LichessRawGame> {
    private final LichessPlayerGamesClient client;

    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    public Flux<LichessRawGame> fetch(Player player) {
        return client.fetchGames(player).map(rawGame -> rawGame.withPlayer(player));
    }
}
