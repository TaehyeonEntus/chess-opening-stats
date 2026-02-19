package com.chessopeningstats.backend.infra.client.checkPlayerClient.chesscom;

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
public class ChessComCheckPlayerClient implements CheckPlayerClient {
    private final WebClient chessComCheckPlayerWebClient;

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public boolean checkPlayer(String username) {
        return chessComCheckPlayerWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pub/player/{username}/games/archives")
                        .build(username))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.just(false);
                    } else {
                        return Mono.error(new RemoteApiServerException("Chess.com API 서버 에러: " + response.statusCode()));
                    }
                })
                .blockOptional()
                .orElseThrow(RemoteApiServerException::new);
    }
}
