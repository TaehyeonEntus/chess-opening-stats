package com.chessopeningstats.backend.infra.client.fetchGameClient;

import com.chessopeningstats.backend.domain.Player;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Stream;

public interface FetchGameClient<T> {
    Flux<T> fetchGames(Player Player);
}
