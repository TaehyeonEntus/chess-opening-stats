package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playergames.PlayerGameClient;
import com.chessopeningstats.backend.infra.client.playergames.dto.ChessComRawGame;
import com.chessopeningstats.backend.infra.client.playergames.impl.ChessComPlayerGameClient;
import com.chessopeningstats.backend.service.syncgame.GameFetchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ChessComGameFetchService implements GameFetchService<ChessComRawGame> {
    private final ChessComPlayerGameClient client;

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public PlayerGameClient<ChessComRawGame> client() {
        return this.client;
    }

    @Override
    public Flux<ChessComRawGame> fetch(Player player) {
        return client.fetchGames(player).map(rawGame -> rawGame.withPlayer(player));
    }
}
