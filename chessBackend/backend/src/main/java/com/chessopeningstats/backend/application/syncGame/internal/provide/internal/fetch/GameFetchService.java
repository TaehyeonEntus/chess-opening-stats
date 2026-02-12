package com.chessopeningstats.backend.application.syncGame.internal.provide.internal.fetch;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.infra.client.fetchGameClient.FetchGameClient;

import java.util.List;

public interface GameFetchService<T> {
    FetchGameClient<T> client();

    default List<T> fetch(Account account) {
        return client().fetchGames(account);
    }
}
