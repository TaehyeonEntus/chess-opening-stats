package com.chessopeningstats.backend.infra.client.playerexistence.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerexistence.PlayerExistenceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class ChessComPlayerExistenceClient implements PlayerExistenceClient {
    private final WebClient chessComPlayerExistenceWebClient;

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public WebClient webClient() {
        return this.chessComPlayerExistenceWebClient;
    }

    @Override
    public String uri(String username) {
        return UriComponentsBuilder
                .fromPath("/pub/player/{username}/games/archives")
                .build(username)
                .toString();
    }
}
