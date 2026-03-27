package com.chessopeningstats.backend.infra.client.playergames;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Platform으로부터 게임 내역을 가져오는 클라이언트
 */
public interface PlayerGamesClient<T> {
    Platform platform();

    String uri(Player player);

    Flux<T> fetchGames(Player Player);

    default Retry retryPolicy() {
        return Retry.backoff(5, Duration.ofSeconds(5))
                .maxBackoff(Duration.ofSeconds(30))
                .filter(e -> e instanceof WebClientResponseException.TooManyRequests);
    }
}
