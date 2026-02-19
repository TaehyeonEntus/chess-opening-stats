package com.chessopeningstats.backend.infra.client.checkPlayerClient.lichess;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.RemoteApiServerException;
import com.chessopeningstats.backend.infra.client.checkPlayerClient.CheckPlayerClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LichessCheckPlayerClient implements CheckPlayerClient {
    private final WebClient lichessCheckPlayerWebClient;

    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    public boolean checkPlayer(String username) {
        return lichessCheckPlayerWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/games/user/{username}")
                        .queryParam("max", 1)
                        .build(username)
                )
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.just(false);
                    } else {
                        return Mono.error(new RemoteApiServerException("Lichess API 서버 에러: " + response.statusCode()));
                    }
                })
                .blockOptional()
                .orElseThrow(RemoteApiServerException::new);
    }
}
