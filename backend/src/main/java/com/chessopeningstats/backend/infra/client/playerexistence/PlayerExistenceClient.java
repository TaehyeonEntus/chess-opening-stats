package com.chessopeningstats.backend.infra.client.playerexistence;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.ExternalServiceException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Platform으로부터 유저 여부를 확인할 수 있는 클라이언트
 */
public interface PlayerExistenceClient {
    Platform platform();

    WebClient webClient();

    String uri(String username);

    default boolean existsUsername(String username) {
        return Boolean.TRUE.equals(webClient()
                .get()
                .uri(uri(username))
                .retrieve()
                .toBodilessEntity()
                .retryWhen(retryPolicy())
                .map(response -> true)
                .onErrorReturn(WebClientResponseException.NotFound.class, false)
                .onErrorMap(ExternalServiceException::new)
                .block());
    }

    default Retry retryPolicy() {
        return Retry.backoff(5, Duration.ofSeconds(5))
                .maxBackoff(Duration.ofSeconds(30))
                .filter(e -> e instanceof WebClientResponseException.TooManyRequests);
    }
}
