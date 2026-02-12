package com.chessopeningstats.backend.infra.client.fetchGameClient;

import com.chessopeningstats.backend.domain.Account;

import java.util.List;

public interface FetchGameClient<T> {
    List<T> fetchGames(Account account);
}
