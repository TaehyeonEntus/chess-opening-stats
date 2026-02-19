package com.chessopeningstats.backend.infra.client.fetchGameClient;

import com.chessopeningstats.backend.domain.Player;

import java.util.List;

public interface FetchGameClient<T> {
    List<T> fetchGames(Player Player);
}
