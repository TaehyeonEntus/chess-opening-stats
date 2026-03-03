package com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.fetch;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.fetchGameClient.FetchGameClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

public interface GameFetchService<T> {
    FetchGameClient<T> client();

    default Flux<T> fetch(Player Player) {
        return client().fetchGames(Player);
    }
}
