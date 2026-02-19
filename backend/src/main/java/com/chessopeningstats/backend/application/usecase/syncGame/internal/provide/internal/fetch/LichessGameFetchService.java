package com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.fetch;

import com.chessopeningstats.backend.infra.client.fetchGameClient.FetchGameClient;
import com.chessopeningstats.backend.infra.client.fetchGameClient.lichess.LichessFetchGameClient;
import com.chessopeningstats.backend.infra.client.fetchGameClient.lichess.LichessGameDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LichessGameFetchService implements GameFetchService<LichessGameDto> {
    private final LichessFetchGameClient client;

    @Override
    public FetchGameClient<LichessGameDto> client() {
        return client;
    }
}
