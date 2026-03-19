package com.chessopeningstats.backend.infra.client.playerexistence.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerexistence.PlayerExistenceClient;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.ChessComPlayerExistence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class ChessComPlayerExistenceClient implements PlayerExistenceClient<ChessComPlayerExistence> {
    private final RestClient chessComPlayerExistenceRestClient;

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public RestClient client() {
        return this.chessComPlayerExistenceRestClient;
    }

    @Override
    public String uri(String username) {
        return UriComponentsBuilder
                .fromPath("/pub/player/{username}")
                .build(username)
                .toString();
    }

    @Override
    public Class<ChessComPlayerExistence> type() {
        return ChessComPlayerExistence.class;
    }
}
