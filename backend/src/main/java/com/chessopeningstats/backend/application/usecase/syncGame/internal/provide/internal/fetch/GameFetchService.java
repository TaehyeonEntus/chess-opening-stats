package com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.fetch;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.fetchGameClient.FetchGameClient;

import java.util.List;

public interface GameFetchService<T> {
    FetchGameClient<T> client();

    default List<T> fetch(Player Player) {
        return client().fetchGames(Player);
    }
}
