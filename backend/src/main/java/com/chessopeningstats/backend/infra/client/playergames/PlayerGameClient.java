package com.chessopeningstats.backend.infra.client.playergames;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.ParallelFlux;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Platform으로부터 게임 내역을 가져오는 클라이언트
 */
public interface PlayerGameClient<T extends RawGame> {
    Platform platform();

    WebClient client();

    String uri(Player player);

    ParallelFlux<T> fetchGames(Player Player);

    default Retry retryPolicy() {
        return Retry.backoff(5, Duration.ofSeconds(5))
                .maxBackoff(Duration.ofSeconds(30))
                .filter(e -> e instanceof WebClientResponseException.TooManyRequests);
    }
}
