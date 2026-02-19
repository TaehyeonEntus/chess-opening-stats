package com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.fetch;

import com.chessopeningstats.backend.infra.client.fetchGameClient.FetchGameClient;
import com.chessopeningstats.backend.infra.client.fetchGameClient.chesscom.ChessComFetchGameClient;
import com.chessopeningstats.backend.infra.client.fetchGameClient.chesscom.ChessComGameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChessComGameFetchService implements GameFetchService<ChessComGameDto> {
    private final ChessComFetchGameClient client;

    @Override
    public FetchGameClient<ChessComGameDto> client() {
        return client;
    }
}
